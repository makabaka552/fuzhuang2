package order.service.impl;

import lombok.extern.slf4j.Slf4j;
import model.*;
import order.mapper.*;
import order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void AddOrder(String username, Order order) {
        User user = userMapper.findByUsername(username);
        order.setUserId(user.getId());
        Addresses address = addressMapper.findById(order.getAddressId())
                .orElseThrow(() -> new RuntimeException("该地址不存在"));
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getAddress());
        order.setCreatedTime(LocalDateTime.now());

        // 检查并减少库存
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Products product = productMapper.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("商品不存在"));

                // 设置订单项的图片URL
                item.setImageUrl(product.getImageUrl());
                item.setOrder(order);

                // 检查是否有变体ID
                Integer variantId = item.getVariantId();
                if (variantId == null) {
                    log.error("订单项缺少变体ID，无法减少库存: productId={}", item.getProductId());
                    throw new RuntimeException("订单项缺少变体ID，无法下单");
                }

                // 通过变体ID获取颜色和尺码信息
                try {
                    // 调用产品服务获取变体信息
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("variantId", variantId);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                    // 调用产品服务获取变体详情
                    ResponseEntity<Map> response = restTemplate.getForEntity(
                            "http://localhost:8082/products/variant-detail/" + variantId,
                            Map.class);

                    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                        log.error("获取变体信息失败: variantId={}", variantId);
                        throw new RuntimeException("获取变体信息失败，无法下单");
                    }

                    Map<String, Object> responseBody = response.getBody();
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

                    if (data == null) {
                        log.error("变体信息为空: variantId={}", variantId);
                        throw new RuntimeException("变体信息为空，无法下单");
                    }

                    // 从变体信息中获取颜色ID和尺码ID
                    Integer colorId = (Integer) data.get("colorId");
                    Integer sizeId = (Integer) data.get("sizeId");

                    if (colorId == null || sizeId == null) {
                        log.error("变体信息不完整: variantId={}, colorId={}, sizeId={}",
                                variantId, colorId, sizeId);
                        throw new RuntimeException("变体信息不完整，无法下单");
                    }

                    // 减少库存
                    boolean stockUpdated = decreaseProductStock(item.getProductId(), colorId, sizeId,
                            item.getQuantity());

                    if (!stockUpdated) {
                        throw new RuntimeException("商品库存不足，无法下单");
                    }
                } catch (Exception e) {
                    log.error("处理变体信息失败: {}", e.getMessage(), e);
                    throw new RuntimeException("处理变体信息失败: " + e.getMessage());
                }
            }
        }

        // 保存订单
        orderMapper.save(order);
    }

    /**
     * 调用产品服务减少库存
     */
    private boolean decreaseProductStock(Long productId, Integer colorId, Integer sizeId, Integer quantity) {
        try {
            log.info("调用产品服务减少库存: productId={}, colorId={}, sizeId={}, quantity={}",
                    productId, colorId, sizeId, quantity);

            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("productId", productId);
            requestBody.put("colorId", colorId);
            requestBody.put("sizeId", sizeId);
            requestBody.put("quantity", quantity);

            // 创建HTTP实体
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用产品服务的减少库存API
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8082/products/decrease-stock",
                    entity,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return Boolean.TRUE.equals(responseBody.get("success"));
            }

            return false;
        } catch (Exception e) {
            log.error("调用产品服务减少库存失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Order GetOrder(String username, Long id) {
        User user = userMapper.findByUsername(username);
        Order order = orderMapper.findById(id).orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("该订单不属于该用户");
        }

        return order;
    }

    @Override
    public List<Order> GetAllOrder(String username) {
        User user = userMapper.findByUsername(username);
        Long userid = user.getId();
        List<Order> orders = orderMapper.findByuserId(userid);
        return orders;
    }

    @Override
    public void cancel(String username, Long id) {
        User user = userMapper.findByUsername(username);
        Order order = orderMapper.getById(id);
        if (order == null) {
            System.out.println("未找到订单"); // 添加日志
            throw new RuntimeException("该订单不存在");
        }
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("该订单不属于该用户");
        }
        if (!"UNPAID".equals(order.getStatus())) {
            System.out.println("订单状态不是UNPAID: " + order.getStatus()); // 添加日志
            throw new RuntimeException("该订单状态不是未支付");
        }
        // 7. 取消订单
        order.setStatus("CANCELLED");
        try {
            // 重新获取完整的订单信息（包括订单项）
            Order fullOrder = orderMapper.findById(order.getId())
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            // 如果订单状态变为已取消，需要恢复商品库存
            if ("CANCELLED".equals(order.getStatus())) {
                List<OrderItem> items = fullOrder.getItems();
                if (items != null) {
                    for (OrderItem item : items) {
                        try {
                            Optional<Products> productOpt = productMapper.findById(item.getProductId());
                            if (productOpt.isPresent()) {
                                Products product = productOpt.get();
                                // 恢复库存
                                product.setStock(product.getStock() + item.getQuantity());
                                productMapper.save(product);
                            } else {
                                // 记录日志但不中断流程
                                log.warn("取消订单时商品不存在，商品ID: {}", item.getProductId());
                            }
                        } catch (Exception e) {
                            // 记录日志但不中断流程
                            log.error("恢复商品库存失败，商品ID: {}, 错误: {}", item.getProductId(), e.getMessage());
                        }
                    }
                }
            }

            // 更新订单状态
            fullOrder.setStatus(order.getStatus());
            orderMapper.save(fullOrder);
        } catch (Exception e) {
            log.error("取消订单失败，订单ID: {}, 错误: {}", order.getId(), e.getMessage());
            throw new RuntimeException("取消订单失败：" + e.getMessage());
        }
    }

    @Override
    public void DeleteOrder(String username, Long id) {
        User user = userMapper.findByUsername(username);
        Order order = orderMapper.getById(id);
        if (order == null) {
            System.out.println("未找到订单");
            throw new RuntimeException("该订单不存在");
        }
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("该订单不属于该用户");
        }
        // 允许CANCELLED、COMPLETED、REVIEWED状态删除
        if (!("CANCELLED".equals(order.getStatus())
                || "COMPLETED".equals(order.getStatus())
                || "REVIEWED".equals(order.getStatus()))) {
            System.out.println("订单状态不是CANCELLED/COMPLETED/REVIEWED: " + order.getStatus());
            throw new RuntimeException("该订单状态不可删除");
        }
        orderMapper.deleteById(id);
    }

    @Override
    public Order updateOrderStatus(String orderNo, OrderStatus status) {
        log.info("更新订单状态: 订单号={}, 新状态={}", orderNo, status.getCode());

        // 获取订单
        Order order = getOrderById(orderNo);

        // 更新状态
        order.setStatus(status.getCode());

        // 根据状态设置相应的时间字段
        if (OrderStatus.PAID.equals(status)) {
            order.setPaymentTime(LocalDateTime.now());
        } else if (OrderStatus.SHIPPED.equals(status)) {
            order.setShipTime(LocalDateTime.now());
        } else if (OrderStatus.COMPLETED.equals(status)) {
            order.setCompleteTime(LocalDateTime.now());
        }

        // 保存更新
        return orderMapper.save(order);
    }

    @Override
    public Order handlePaymentSuccess(String orderNo, String paymentMethod, LocalDateTime payTime) {
        log.info("处理支付成功: 订单号={}, 支付方式={}, 支付时间={}", orderNo, paymentMethod, payTime);

        // 获取订单
        Order order = getOrderById(orderNo);

        // 检查订单状态是否允许支付
        if (!OrderStatus.UNPAID.getCode().equals(order.getStatus()) &&
                !OrderStatus.PAYMENT_PROCESSING.getCode().equals(order.getStatus())) {
            log.warn("订单状态不允许支付: 订单号={}, 当前状态={}", orderNo, order.getStatus());
            throw new RuntimeException("订单状态不允许支付: " + order.getStatus());
        }

        // 更新订单状态和支付信息
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(payTime);

        // 保存更新
        Order savedOrder = orderMapper.save(order);

        // 处理优惠券使用
        if (order.getCouponId() != null) {
            try {
                useCoupon(order.getCouponId());
                log.info("优惠券使用成功: couponId={}", order.getCouponId());
            } catch (Exception e) {
                log.error("优惠券使用失败: couponId={}, error={}", order.getCouponId(), e.getMessage());
            }
        }

        // 支付成功后给用户添加积分（支付多少元获得多少积分）
        try {
            BigDecimal totalAmount = order.getTotalAmount();
            if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                int points = totalAmount.intValue();
                addPointsToUser(order.getUserId(), points);
                log.info("用户获得积分: userId={}, points={}", order.getUserId(), points);
            }
        } catch (Exception e) {
            log.error("添加积分失败: userId={}, error={}", order.getUserId(), e.getMessage());
        }

        return savedOrder;
    }

    private void useCoupon(Long couponId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Long> requestBody = new HashMap<>();
            requestBody.put("couponId", couponId);
            
            HttpEntity<Map<String, Long>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8091/points/coupon/use",
                    entity,
                    Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("调用积分服务使用优惠券失败: couponId={}", couponId);
            }
        } catch (Exception e) {
            log.error("调用积分服务使用优惠券异常: {}", e.getMessage());
        }
    }

    private void addPointsToUser(Long userId, int points) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", userId);
            requestBody.put("points", points);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8091/points/add",
                    entity,
                    Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("调用积分服务添加积分失败: userId={}, points={}", userId, points);
            }
        } catch (Exception e) {
            log.error("调用积分服务添加积分异常: {}", e.getMessage());
        }
    }

    @Override
    public Order getOrderById(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            log.error("未找到订单: {}", orderNo);
            throw new RuntimeException("订单不存在: " + orderNo);
        }
        return order;
    }

    @Override
    public Order updateOrder(Order order) {
        log.info("更新订单: 订单号={}, 状态={}", order.getOrderNo(), order.getStatus());

        // 检查订单是否存在
        if (order.getId() == null) {
            log.error("更新订单失败: 订单ID为空");
            throw new RuntimeException("更新订单失败: 订单ID为空");
        }

        // 保存更新
        return orderMapper.save(order);
    }

    @Override
    public PageBeam GetAllOrder(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        // 使用 JPA 分页查询
        Page<Order> orders = orderMapper.findAll(pageable); // 确保 orderRepository 是 JPA Repository
        PageBeam pageBeam = new PageBeam();
        pageBeam.setTotal(orders.getTotalElements()); // 总记录数
        pageBeam.setRows(orders.getContent()); // 当前页数据
        return pageBeam;
    }

    @Override
    public Map<String, Object> getOrderStats() {
        // 1. 订单总数
        long total = orderMapper.count();

        // 2. 总金额（假设订单有 `amount` 字段）
        Double totalAmount = orderMapper.sumTotalAmount();
        if (totalAmount == null)
            totalAmount = 0.0;

        // 3. 按状态统计
        List<Object[]> statusCounts = orderMapper.countByStatus();
        Map<String, Integer> statusCountsMap = new HashMap<>();
        statusCounts.forEach(arr -> {
            statusCountsMap.put((String) arr[0], ((Long) arr[1]).intValue());
        });

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("totalAmount", totalAmount);
        result.put("statusCounts", statusCountsMap);
        return result;
    }

    @Override
    public void cancelOrder(String orderno) {
        Order order = orderMapper.findByOrderNo(orderno);
        if (order == null) {
            System.out.println("未找到订单"); // 添加日志
            throw new RuntimeException("该订单不存在");
        }
        if (!"UNPAID".equals(order.getStatus())) {
            System.out.println("订单状态不是UNPAID: " + order.getStatus()); // 添加日志
            throw new RuntimeException("该订单状态不是未支付");
        }
        // 7. 取消订单
        order.setStatus("CANCELLED");
        try {
            // 重新获取完整的订单信息（包括订单项）
            Order fullOrder = orderMapper.findById(order.getId())
                    .orElseThrow(() -> new RuntimeException("订单不存在"));

            // 如果订单状态变为已取消，需要恢复商品库存
            if ("CANCELLED".equals(order.getStatus())) {
                List<OrderItem> items = fullOrder.getItems();
                if (items != null) {
                    for (OrderItem item : items) {
                        try {
                            Optional<Products> productOpt = productMapper.findById(item.getProductId());
                            if (productOpt.isPresent()) {
                                Products product = productOpt.get();
                                // 恢复库存
                                product.setStock(product.getStock() + item.getQuantity());
                                productMapper.save(product);
                            } else {
                                // 记录日志但不中断流程
                                log.warn("取消订单时商品不存在，商品ID: {}", item.getProductId());
                            }
                        } catch (Exception e) {
                            // 记录日志但不中断流程
                            log.error("恢复商品库存失败，商品ID: {}, 错误: {}", item.getProductId(), e.getMessage());
                        }
                    }
                }
            }

            // 更新订单状态
            fullOrder.setStatus(order.getStatus());
            orderMapper.save(fullOrder);
        } catch (Exception e) {
            log.error("取消订单失败，订单ID: {}, 错误: {}", order.getId(), e.getMessage());
            throw new RuntimeException("取消订单失败：" + e.getMessage());
        }
    }
}

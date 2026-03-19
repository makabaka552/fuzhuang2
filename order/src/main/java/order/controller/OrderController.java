package order.controller;

import lombok.extern.slf4j.Slf4j;
import model.Order;
import model.OrderStatus;
import model.Result;
import order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    @Autowired
    private OrderService orderService;

    @PostMapping("/addorder")
    public Result AddOrder(@RequestBody Order order, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        orderService.AddOrder(username, order);
        return Result.success("已成功添加订单");
    }

    @GetMapping("/{id}")
    public Result GetOrder(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        Order order = orderService.GetOrder(username, id);
        return Result.success(order);
    }

    @GetMapping("/listorder")
    public Result getAllOrders(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        List<Order> order = orderService.GetAllOrder(username);
        return Result.success(order);

    }

    @PostMapping("/{id}/cancel")
    public Result CancelOrder(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        orderService.cancel(username, id);
        return Result.success("取消订单成功");
    }

    @DeleteMapping("/{id}")
    public Result DeleteOrder(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        orderService.DeleteOrder(username, id);
        return Result.success("删除订单成功");
    }

    @RequestMapping("/status/update")
    public Result updateOrderStatus(@RequestParam String orderNo, @RequestParam String status) {

        Map<String, Object> response = new HashMap<>();
        try {
            log.info("接收到订单状态更新请求: 订单号={}, 新状态={}", orderNo, status);
            OrderStatus orderStatus = OrderStatus.fromCode(status);
            if (orderStatus == null) {
                throw new IllegalArgumentException("无效的订单状态: " + status);
            }
            Order order = orderService.updateOrderStatus(orderNo, orderStatus);
            response.put("success", true);
            response.put("message", "订单状态更新成功");
            response.put("orderNo", order.getOrderNo());
            response.put("newStatus", order.getStatus());
            log.info("订单状态更新成功: 订单号={}, 新状态={}", orderNo, order.getStatus());
        } catch (Exception e) {
            log.error("订单状态更新失败: 订单号={}, 错误={}", orderNo, e.getMessage());
            response.put("success", false);
            response.put("message", "订单状态更新失败: " + e.getMessage());
        }
        return Result.success(response);
    }

    @RequestMapping("/payment/notify")
    public Result paymentNotify(@RequestParam String orderNo, @RequestParam String paymentMethod,
            @RequestParam(required = false) String paymentTime, @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("接收到支付通知: 订单号={}, 支付方式={}, 支付时间={}, 状态={}",
                    orderNo, paymentMethod, paymentTime, status);

            // 将支付时间字符串转换为LocalDateTime
            LocalDateTime payTime = null;
            if (paymentTime != null && !paymentTime.isEmpty()) {
                try {
                    payTime = LocalDateTime.parse(paymentTime, formatter);
                } catch (Exception e) {
                    log.warn("支付时间格式无效，使用当前时间: {}", paymentTime);
                    payTime = LocalDateTime.now();
                }
            } else {
                payTime = LocalDateTime.now();
            }

            // 处理支付成功逻辑
            Order updatedOrder = orderService.handlePaymentSuccess(orderNo, paymentMethod, payTime);

            response.put("success", true);
            response.put("message", "支付通知处理成功");
            response.put("orderNo", updatedOrder.getOrderNo());
            response.put("newStatus", updatedOrder.getStatus());

            log.info("支付通知处理成功: 订单号={}, 新状态={}", orderNo, updatedOrder.getStatus());
        } catch (Exception e) {
            log.error("支付通知处理失败: 订单号={}, 错误={}", orderNo, e.getMessage());
            response.put("success", false);
            response.put("message", "支付通知处理失败: " + e.getMessage());
        }

        return Result.success(response);
    }

    @PostMapping("/ship")
    public Result shipOrder(@RequestParam String orderNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("手动发货请求: 订单号={}", orderNo);

            // 查询订单
            Order order = orderService.getOrderById(orderNo);

            // 检查订单状态
            if (!OrderStatus.PAID.getCode().equals(order.getStatus())) {
                response.put("success", false);
                response.put("message", "只有已支付的订单才能发货，当前状态: " + order.getStatus());
                return Result.success(response);
            }

            // 更新订单状态为已发货
            order.setStatus(OrderStatus.SHIPPED.getCode());
            order.setShipTime(LocalDateTime.now());
            Order updatedOrder = orderService.updateOrder(order);

            response.put("success", true);
            response.put("message", "订单发货成功");
            response.put("orderNo", updatedOrder.getOrderNo());
            response.put("newStatus", updatedOrder.getStatus());

            log.info("订单发货成功: 订单号={}", orderNo);
        } catch (Exception e) {
            log.error("订单发货失败: 订单号={}, 错误={}", orderNo, e.getMessage());
            response.put("success", false);
            response.put("message", "订单发货失败: " + e.getMessage());
        }

        return Result.success(response);
    }

    @PostMapping("/complete")
    public Result completeOrder(@RequestParam String orderNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("完成订单请求: 订单号={}", orderNo);

            // 查询订单
            Order order = orderService.getOrderById(orderNo);

            // 检查订单状态
            if (!OrderStatus.SHIPPED.getCode().equals(order.getStatus())) {
                response.put("success", false);
                response.put("message", "只有已发货的订单才能完成，当前状态: " + order.getStatus());
                return Result.success(response);
            }

            // 更新订单状态为已完成
            order.setStatus(OrderStatus.COMPLETED.getCode());
            order.setCompleteTime(LocalDateTime.now());
            Order updatedOrder = orderService.updateOrder(order);

            response.put("success", true);
            response.put("message", "订单完成成功");
            response.put("orderNo", updatedOrder.getOrderNo());
            response.put("newStatus", updatedOrder.getStatus());

            log.info("订单完成成功: 订单号={}", orderNo);
        } catch (Exception e) {
            log.error("订单完成失败: 订单号={}, 错误={}", orderNo, e.getMessage());
            response.put("success", false);
            response.put("message", "订单完成失败: " + e.getMessage());
        }

        return Result.success(response);
    }

}

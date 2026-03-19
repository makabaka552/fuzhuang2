package payment.service;

import model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import payment.entity.PaymentRecord;

import payment.enums.PaymentStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单支付状态映射服务
 * 负责处理订单系统和支付系统之间的状态映射
 */
@Service
public class OrderPaymentMappingService {

    @Value("${alipay.notify-url}")
    private String NotifyUrl;

    private static final Logger logger = LoggerFactory.getLogger(OrderPaymentMappingService.class);

    @Autowired
    private RestTemplate restTemplate;

    // 订单状态映射
    private static final Map<String, String> PAYMENT_TO_ORDER_STATUS_MAP = new HashMap<>();

    static {
        // 初始化支付状态到订单状态的映射
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.PENDING.getCode(), OrderStatus.UNPAID.getCode());
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.PROCESSING.getCode(), OrderStatus.PAYMENT_PROCESSING.getCode());
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.SUCCESS.getCode(), OrderStatus.PAID.getCode());
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.FAILED.getCode(), OrderStatus.PAYMENT_FAILED.getCode());
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.CANCELLED.getCode(), OrderStatus.CANCELLED.getCode());
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.REFUNDED.getCode(), OrderStatus.REFUNDED.getCode());
        PAYMENT_TO_ORDER_STATUS_MAP.put(PaymentStatus.PARTIAL_REFUNDED.getCode(),
                OrderStatus.PARTIAL_REFUNDED.getCode());
    }

    /**
     * 将支付状态映射为订单状态
     *
     * @param paymentStatus 支付状态
     * @return 订单状态
     */
    public String mapPaymentStatusToOrderStatus(String paymentStatus) {
        String orderStatus = PAYMENT_TO_ORDER_STATUS_MAP.get(paymentStatus);
        if (orderStatus == null) {
            logger.warn("未找到支付状态[{}]对应的订单状态，使用默认状态UNPAID", paymentStatus);
            return OrderStatus.UNPAID.getCode();
        }
        return orderStatus;
    }

    /**
     * 更新订单状态
     *
     * @param orderNo       订单号
     * @param paymentStatus 支付状态
     * @return 更新结果
     */
    public boolean updateOrderStatus(String orderNo, String paymentStatus) {
        String orderStatus = mapPaymentStatusToOrderStatus(paymentStatus);
        logger.info("准备更新订单状态: 订单号={}, 支付状态={}, 订单状态={}", orderNo, paymentStatus, orderStatus);

        try {
            // URL编码参数
            String encodedOrderNo = java.net.URLEncoder.encode(orderNo, "UTF-8");
            String encodedStatus = java.net.URLEncoder.encode(orderStatus, "UTF-8");

            // 构造URL参数
            String url = "http://localhost:8086/orders/status/update?orderNo=" + encodedOrderNo + "&status="
                    + encodedStatus;
            logger.info("发送订单状态更新请求: URL={}", url);

            // 使用GET方式请求，参数通过URL传递
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            logger.info("订单状态更新响应: {}", response);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                logger.info("订单状态更新成功: {}", orderNo);
                return true;
            } else {
                logger.error("订单状态更新失败: {}, 响应: {}", orderNo, response);

                // 尝试使用另一种方式进行回调
                logger.info("尝试使用支付通知接口作为备选方案更新订单状态");
                try {
                    // 构造支付通知URL参数，使用URL编码http://sb9772df.natappfree.cc/payment/alipay/notify
                    String notifyUrl = NotifyUrl + "?orderNo="
                            + encodedOrderNo +
                            "&paymentMethod=ALIPAY&status=" + encodedStatus;
                    logger.info("尝试使用支付通知接口: URL={}", notifyUrl);

                    Map<String, Object> notifyResponse = restTemplate.getForObject(notifyUrl, Map.class);
                    logger.info("支付通知响应: {}", notifyResponse);

                    if (notifyResponse != null && Boolean.TRUE.equals(notifyResponse.get("success"))) {
                        logger.info("通过支付通知接口更新订单状态成功: {}", orderNo);
                        return true;
                    } else {
                        logger.error("通过支付通知接口更新订单状态失败: {}, 响应: {}", orderNo, notifyResponse);
                    }
                } catch (Exception e) {
                    logger.error("通过支付通知接口更新订单状态异常: " + orderNo, e);
                    logger.error("支付通知接口异常详情: {}", e.getMessage());
                }

                return false;
            }
        } catch (Exception e) {
            logger.error("更新订单状态异常: " + orderNo, e);
            logger.error("异常类型: {}, 异常消息: {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace(); // 添加详细异常堆栈输出

            // 记录请求详情，便于调试
            logger.error("订单状态更新请求失败，请检查接口URL是否正确，以及目标接口是否可达");
            return false;
        }
    }

    /**
     * 根据支付记录更新订单状态
     *
     * @param paymentRecord 支付记录
     * @return 更新结果
     */
    public boolean updateOrderStatus(PaymentRecord paymentRecord) {
        return updateOrderStatus(paymentRecord.getOrderNo(), paymentRecord.getStatus());
    }
}
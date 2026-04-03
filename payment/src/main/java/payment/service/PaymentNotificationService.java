package payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import payment.entity.PaymentRecord;
import payment.event.PaymentSuccessEvent;
import java.util.Map;

/**
 * 支付通知服务
 * 负责在支付成功后通知订单系统和其他相关系统
 */
@Service
public class PaymentNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificationService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 通过事件通知支付结果
     *
     * @param paymentRecord 支付记录
     */
    public void notifyPaymentResultByEvent(PaymentRecord paymentRecord) {
        logger.info("发布支付成功事件，订单号: {}", paymentRecord.getOrderNo());

        // 创建并发布支付成功事件
        PaymentSuccessEvent event = new PaymentSuccessEvent(this, paymentRecord);
        eventPublisher.publishEvent(event);
    }

    /**
     * 通过REST API通知订单系统
     *
     * @param paymentRecord 支付记录
     * @return 通知结果
     */
    public boolean notifyOrderSystem(PaymentRecord paymentRecord) {
        logger.info("通知订单系统，支付成功，订单号: {}", paymentRecord.getOrderNo());

        try {
            // URL编码参数
            String encodedOrderNo = java.net.URLEncoder.encode(paymentRecord.getOrderNo(), "UTF-8");
            String encodedMethod = java.net.URLEncoder.encode(paymentRecord.getPaymentMethod(), "UTF-8");

            // 构造URL参数
            StringBuilder urlBuilder = new StringBuilder(
                    "http://localhost:8086/orders/payment/notify?");
            urlBuilder.append("orderNo=").append(encodedOrderNo);
            urlBuilder.append("&paymentMethod=").append(encodedMethod);

            // 如果有支付时间，添加到URL
            if (paymentRecord.getPaymentTime() != null) {
                String encodedTime = java.net.URLEncoder.encode(paymentRecord.getPaymentTime().toString(), "UTF-8");
                urlBuilder.append("&paymentTime=").append(encodedTime);
            }

            // 添加状态参数
            urlBuilder.append("&status=PAID");

            String url = urlBuilder.toString();
            logger.info("通知订单系统URL: {}", url);

            // 发送GET请求
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                logger.info("通知订单系统成功");
                return true;
            } else {
                logger.error("通知订单系统失败: {}", response);
                return false;
            }
        } catch (Exception e) {
            logger.error("通知订单系统异常", e);
            return false;
        }
    }
}
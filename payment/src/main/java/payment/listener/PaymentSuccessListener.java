package payment.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import payment.entity.PaymentRecord;
import payment.event.PaymentSuccessEvent;

/**
 * 支付成功事件监听器
 * 用于监听支付成功事件并做出响应
 */
@Component
public class PaymentSuccessListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSuccessListener.class);

    /**
     * 监听支付成功事件
     *
     * @param event 支付成功事件
     */
    @EventListener
    public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
        PaymentRecord paymentRecord = event.getPaymentRecord();
        logger.info("收到支付成功事件: 订单号={}, 支付方式={}, 金额={}",
                paymentRecord.getOrderNo(),
                paymentRecord.getPaymentMethod(),
                paymentRecord.getAmount());

        // 在这里可以做一些自定义的业务处理
        // 例如发送通知给用户、更新订单状态等
    }
}
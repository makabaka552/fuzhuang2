package payment.event;

import org.springframework.context.ApplicationEvent;
import payment.entity.PaymentRecord;

/**
 * 支付成功事件
 * 在支付成功后触发，用于通知其他系统
 */
public class PaymentSuccessEvent extends ApplicationEvent {

    private final PaymentRecord paymentRecord;

    /**
     * 创建一个新的支付成功事件
     *
     * @param source        事件源
     * @param paymentRecord 支付记录
     */
    public PaymentSuccessEvent(Object source, PaymentRecord paymentRecord) {
        super(source);
        this.paymentRecord = paymentRecord;
    }

    /**
     * 获取支付记录
     *
     * @return 支付记录
     */
    public PaymentRecord getPaymentRecord() {
        return paymentRecord;
    }
}
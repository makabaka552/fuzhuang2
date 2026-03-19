package payment.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 用于存储订单支付相关的信息
 */
@Entity
@Table(name = "payment_records")
public class PaymentRecord {

    /**
     * 支付记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单编号
     */
    @Column(name = "order_no", nullable = false)
    private String orderNo;

    /**
     * 支付交易号
     * 支付宝/微信等平台生成的交易号
     */
    @Column(name = "trade_no")
    private String tradeNo;

    /**
     * 支付金额
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * 支付方式
     * 例如：ALIPAY-支付宝, WECHAT-微信支付
     */
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    /**
     * 支付状态
     * PENDING-待支付, SUCCESS-支付成功, FAILED-支付失败
     */
    @Column(nullable = false)
    private String status;

    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    /**
     * 支付完成时间
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 额外信息(JSON格式)
     * 存储支付的额外信息
     */
    @Column(name = "extra_info", columnDefinition = "TEXT")
    private String extraInfo;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
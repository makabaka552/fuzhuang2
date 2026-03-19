package payment.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class RefundRequest {
    private String orderNo;
    private BigDecimal refundAmount;
    private String refundReason;

    // Getter 和 Setter
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    // 可选：重写 toString() 方便日志打印
    @Override
    public String toString() {
        return "RefundRequest{" +
                "orderNo='" + orderNo + '\'' +
                ", refundAmount=" + refundAmount +
                ", refundReason='" + refundReason + '\'' +
                '}';
    }

    // 可选：重写 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefundRequest that = (RefundRequest) o;
        return Objects.equals(orderNo, that.orderNo) &&
                Objects.equals(refundAmount, that.refundAmount) &&
                Objects.equals(refundReason, that.refundReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNo, refundAmount, refundReason);
    }
}
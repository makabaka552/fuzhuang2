package payment.enums;

/**
 * 支付状态枚举
 * 定义支付流程中各种状态
 */
public enum PaymentStatus {
    /**
     * 待支付：支付记录已创建，等待用户支付
     */
    PENDING("PENDING", "待支付"),

    /**
     * 支付中：用户已发起支付，等待支付平台确认
     */
    PROCESSING("PROCESSING", "支付中"),

    /**
     * 支付成功：支付已完成
     */
    SUCCESS("SUCCESS", "支付成功"),

    /**
     * 支付失败：支付过程中出现错误
     */
    FAILED("FAILED", "支付失败"),

    /**
     * 已取消：支付已被取消
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 已退款：支付成功后被退款
     */
    REFUNDED("REFUNDED", "已退款"),

    /**
     * 部分退款：支付成功后部分退款
     */
    PARTIAL_REFUNDED("PARTIAL_REFUNDED", "部分退款");

    private final String code;
    private final String description;

    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 对应的枚举或null
     */
    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
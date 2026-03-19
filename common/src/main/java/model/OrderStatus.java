package model;

/**
 * 订单状态枚举
 * 定义订单生命周期中的各种状态
 */
public enum OrderStatus {
    /**
     * 待支付：订单已创建，等待支付
     */
    UNPAID("UNPAID", "待支付"),

    /**
     * 支付处理中：订单支付正在处理
     */
    PAYMENT_PROCESSING("PAYMENT_PROCESSING", "支付处理中"),

    /**
     * 已支付：订单已支付成功
     */
    PAID("PAID", "已支付"),

    /**
     * 支付失败：订单支付失败
     */
    PAYMENT_FAILED("PAYMENT_FAILED", "支付失败"),

    /**
     * 待发货：订单已支付，等待发货
     */
    PENDING_SHIPMENT("PENDING_SHIPMENT", "待发货"),

    /**
     * 已发货：订单已发货，等待收货
     */
    SHIPPED("SHIPPED", "已发货"),

    /**
     * 已完成：订单已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 待评价：订单已完成，等待用户评价
     */
    PENDING_REVIEW("PENDING_REVIEW", "待评价"),

    /**
     * 已评价：订单已完成且用户已评价
     */
    REVIEWED("REVIEWED", "已评价"),

    /**
     * 已取消：订单已取消
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 已退款：订单已退款
     */
    REFUNDED("REFUNDED", "已退款"),

    /**
     * 部分退款：订单部分退款
     */
    PARTIAL_REFUNDED("PARTIAL_REFUNDED", "部分退款");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
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
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
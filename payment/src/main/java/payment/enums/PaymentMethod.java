package payment.enums;

/**
 * 支付方式枚举
 * 定义系统支持的支付方式
 */
public enum PaymentMethod {
    /**
     * 支付宝支付
     */
    ALIPAY("ALIPAY", "支付宝"),

    /**
     * 微信支付
     */
    WECHAT("WECHAT", "微信支付"),

    /**
     * 银行卡支付
     */
    BANK_CARD("BANK_CARD", "银行卡"),

    /**
     * 余额支付
     */
    BALANCE("BALANCE", "余额支付"),

    /**
     * 货到付款
     */
    COD("COD", "货到付款");

    private final String code;
    private final String description;

    PaymentMethod(String code, String description) {
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
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 对应的枚举或null
     */
    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return null;
    }
}
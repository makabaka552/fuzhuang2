package auth.service;

public interface SmsService {
    
    /**
     * 发送短信验证码
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phoneNumber, String code);
}

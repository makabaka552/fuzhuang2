package auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 生成验证码图片
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @throws IOException 如果生成或输出图片出错
     */
    void generateCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 验证验证码
     * 
     * @param request HTTP请求
     * @param captcha 用户输入的验证码
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    boolean validateCaptcha(HttpServletRequest request, String captcha);

    /**
     * 清理过期的验证码记录
     * 
     * @return 清理的记录数
     */
    int cleanExpiredCaptchas();
}
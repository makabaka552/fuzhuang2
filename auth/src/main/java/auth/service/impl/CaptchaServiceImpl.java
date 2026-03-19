package auth.service.impl;

import auth.mapper.CaptchaMapper;
import auth.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private CaptchaMapper captchaMapper;

    /**
     * 验证码有效期（分钟）
     */
    private static final int CAPTCHA_EXPIRE_MINUTES = 5;

    @Override
    public void generateCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应内容类型
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 创建验证码文本
        String captchaText = generateRandomText(4);
        log.info("生成验证码: {}", captchaText);

        // 将验证码存入session
        HttpSession session = request.getSession(true);
        session.setAttribute("captcha", captchaText);
        session.setAttribute("captchaTime", System.currentTimeMillis());

        // 计算过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, CAPTCHA_EXPIRE_MINUTES);
        Date expireTime = calendar.getTime();

        // 保存验证码到数据库（可选）
        /*
        try {
            String sessionId = session.getId();
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            captchaMapper.insertCaptcha(sessionId, captchaText, ipAddress, userAgent, expireTime);
        } catch (Exception e) {
            log.error("保存验证码到数据库失败", e);
            // 继续处理，即使数据库操作失败，验证码仍然可以使用（基于session）
        }*/

        // 创建图像
        BufferedImage image = new BufferedImage(120, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 120, 40);

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)));
            g.drawLine(
                    (int) (Math.random() * 120),
                    (int) (Math.random() * 40),
                    (int) (Math.random() * 120),
                    (int) (Math.random() * 40));
        }

        // 添加噪点
        for (int i = 0; i < 30; i++) {
            int x = (int) (Math.random() * 120);
            int y = (int) (Math.random() * 40);
            g.setColor(new Color(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)));
            g.fillRect(x, y, 2, 2);
        }

        // 绘制文本
        g.setColor(new Color(0, 0, 0));
        g.setFont(new Font("Arial", Font.BOLD, 28));

        // 随机旋转文字
        for (int i = 0; i < captchaText.length(); i++) {
            double theta = Math.random() * 0.3 - 0.15; // 旋转角度范围 -0.15 ~ 0.15
            g.rotate(theta, 10 + i * 25, 28);
            g.drawString(String.valueOf(captchaText.charAt(i)), 10 + i * 25, 28);
            g.rotate(-theta, 10 + i * 25, 28);
        }

        g.dispose();

        // 输出图像
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

    @Override
    public boolean validateCaptcha(HttpServletRequest request, String userCaptcha) {
        if (userCaptcha == null || userCaptcha.isEmpty()) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String captcha = (String) session.getAttribute("captcha");
        Long captchaTime = (Long) session.getAttribute("captchaTime");

        // 验证码使用后立即失效
        session.removeAttribute("captcha");
        session.removeAttribute("captchaTime");

        // 验证码为空或已过期
        if (captcha == null || captchaTime == null) {
            return false;
        }

        // 验证码有效期5分钟
        if (System.currentTimeMillis() - captchaTime > CAPTCHA_EXPIRE_MINUTES * 60 * 1000) {
            return false;
        }

        // 验证码不区分大小写
        boolean isValid = captcha.equalsIgnoreCase(userCaptcha);

        // 如果使用数据库存储验证码，可以在这里标记验证码为已使用
        if (isValid) {
            try {
                CaptchaMapper.CaptchaHistory captchaHistory = captchaMapper.findLatestCaptcha(session.getId());
                if (captchaHistory != null) {
                    captchaMapper.markCaptchaAsUsed(captchaHistory.getId());
                }
            } catch (Exception e) {
                log.error("标记验证码为已使用失败", e);
                // 继续处理，即使数据库操作失败
            }
        }

        return isValid;
    }

    @Override
    @Scheduled(cron = "0 0 */1 * * ?") // 每小时执行一次
    public int cleanExpiredCaptchas() {
        try {
            int count = captchaMapper.cleanExpiredCaptchas();
            log.info("清理过期验证码记录: {} 条", count);
            return count;
        } catch (Exception e) {
            log.error("清理过期验证码记录失败", e);
            return 0;
        }
    }

    /**
     * 生成随机验证码文本
     */
    private String generateRandomText(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
package auth.mapper;

import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface CaptchaMapper {

    /**
     * 插入验证码记录
     */
    @Insert("INSERT INTO captcha_history(session_id, captcha_code, ip_address, user_agent, expire_time) " +
            "VALUES(#{sessionId}, #{captchaCode}, #{ipAddress}, #{userAgent}, #{expireTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCaptcha(String sessionId, String captchaCode, String ipAddress, String userAgent, Date expireTime);

    /**
     * 根据会话ID查询最新的验证码
     */
    @Select("SELECT * FROM captcha_history WHERE session_id = #{sessionId} AND is_used = 0 AND expire_time > NOW() " +
            "ORDER BY create_time DESC LIMIT 1")
    CaptchaHistory findLatestCaptcha(String sessionId);

    /**
     * 标记验证码为已使用
     */
    @Update("UPDATE captcha_history SET is_used = 1, used_time = NOW() WHERE id = #{id}")
    int markCaptchaAsUsed(Long id);

    /**
     * 清理过期的验证码
     */
    @Delete("DELETE FROM captcha_history WHERE expire_time < NOW()")
    int cleanExpiredCaptchas();

    /**
     * 验证码历史记录实体类
     */
    class CaptchaHistory {
        private Long id;
        private String sessionId;
        private String captchaCode;
        private String ipAddress;
        private String userAgent;
        private Boolean isUsed;
        private Date createTime;
        private Date expireTime;
        private Date usedTime;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getCaptchaCode() {
            return captchaCode;
        }

        public void setCaptchaCode(String captchaCode) {
            this.captchaCode = captchaCode;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public Boolean getIsUsed() {
            return isUsed;
        }

        public void setIsUsed(Boolean isUsed) {
            this.isUsed = isUsed;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }

        public Date getUsedTime() {
            return usedTime;
        }

        public void setUsedTime(Date usedTime) {
            this.usedTime = usedTime;
        }
    }
}
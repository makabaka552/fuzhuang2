-- 创建验证码历史记录表
CREATE TABLE IF NOT EXISTS `captcha_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` varchar(100) NOT NULL COMMENT '会话ID',
    `captcha_code` varchar(10) NOT NULL COMMENT '验证码内容',
    `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
    `is_used` tinyint(1) DEFAULT 0 COMMENT '是否已使用 0-未使用 1-已使用',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expire_time` timestamp NOT NULL COMMENT '过期时间',
    `used_time` timestamp NULL DEFAULT NULL COMMENT '使用时间',
    PRIMARY KEY (`id`),
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '验证码历史记录表';
-- 注意：该表主要用于以下功能：
-- 1. 记录验证码使用历史，便于审计和统计
-- 2. 防止验证码被重复使用
-- 3. 可以通过IP和会话ID分析验证码请求频率，防止机器人攻击
-- 4. 可以设置验证码有效期
-- 
-- 实际使用时可以结合Redis缓存来提高性能
-- ===========================================
-- 积分商城数据库表结构
-- ===========================================

-- 1. 用户表（确保points字段存在）
-- 如果您的user表没有points字段，请先执行以下语句：
-- ALTER TABLE user ADD COLUMN points INT DEFAULT 0 COMMENT '用户积分';

-- 2. 优惠券表
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    code VARCHAR(50) COMMENT '优惠券代码/兑换码',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额(减多少元)',
    min_order_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '最低订单金额(满多少元可用)',
    start_date DATETIME COMMENT '优惠券开始生效日期',
    end_date DATETIME COMMENT '优惠券结束日期',
    status INT DEFAULT 0 COMMENT '优惠券状态: 0-未使用 1-已使用 2-已过期',
    user_id BIGINT COMMENT '所属用户ID',
    required_points INT DEFAULT 0 COMMENT '兑换所需积分',
    description VARCHAR(500) COMMENT '优惠券描述',
    type INT DEFAULT 0 COMMENT '优惠券类型: 0-满减券 1-折扣券 2-无门槛券',
    stock INT DEFAULT 0 COMMENT '库存数量',
    total_stock INT DEFAULT 0 COMMENT '总库存',
    is_active INT DEFAULT 1 COMMENT '是否上架: 0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- ===========================================
-- 插入示例数据
-- ===========================================

-- 示例1：10元无门槛券
INSERT INTO coupon (
    name, 
    discount_amount, 
    min_order_amount, 
    required_points, 
    description, 
    type, 
    stock, 
    total_stock, 
    is_active,
    start_date,
    end_date
) VALUES (
    '10元无门槛券', 
    10.00, 
    0.00, 
    100, 
    '新用户专享，无门槛使用', 
    2, 
    100, 
    100, 
    1,
    '2024-01-01 00:00:00',
    '2026-12-31 23:59:59'
);

-- 示例2：满50减20券
INSERT INTO coupon (
    name, 
    discount_amount, 
    min_order_amount, 
    required_points, 
    description, 
    type, 
    stock, 
    total_stock, 
    is_active,
    start_date,
    end_date
) VALUES (
    '满50减20券', 
    20.00, 
    50.00, 
    200, 
    '满50元即可使用，立减20元', 
    0, 
    50, 
    50, 
    1,
    '2024-01-01 00:00:00',
    '2026-12-31 23:59:59'
);

-- 示例3：满100减50券
INSERT INTO coupon (
    name, 
    discount_amount, 
    min_order_amount, 
    required_points, 
    description, 
    type, 
    stock, 
    total_stock, 
    is_active,
    start_date,
    end_date
) VALUES (
    '满100减50券', 
    50.00, 
    100.00, 
    500, 
    '超值优惠券，满100减50', 
    0, 
    30, 
    30, 
    1,
    '2024-01-01 00:00:00',
    '2026-12-31 23:59:59'
);

-- 示例4：5元无门槛券
INSERT INTO coupon (
    name, 
    discount_amount, 
    min_order_amount, 
    required_points, 
    description, 
    type, 
    stock, 
    total_stock, 
    is_active,
    start_date,
    end_date
) VALUES (
    '5元无门槛券', 
    5.00, 
    0.00, 
    50, 
    '人人都能兑，5元优惠券', 
    2, 
    200, 
    200, 
    1,
    '2024-01-01 00:00:00',
    '2026-12-31 23:59:59'
);

-- 示例5：满200减100券
INSERT INTO coupon (
    name, 
    discount_amount, 
    min_order_amount, 
    required_points, 
    description, 
    type, 
    stock, 
    total_stock, 
    is_active,
    start_date,
    end_date
) VALUES (
    '满200减100券', 
    100.00, 
    200.00, 
    1000, 
    '大额优惠券，满200减100', 
    0, 
    20, 
    20, 
    1,
    '2024-01-01 00:00:00',
    '2026-12-31 23:59:59'
);

-- 为用户表添加一些积分（如果用户表存在的话）
-- 假设用户ID为1、2、3的用户
-- UPDATE user SET points = 1000 WHERE id = 1;
-- UPDATE user SET points = 500 WHERE id = 2;
-- UPDATE user SET points = 200 WHERE id = 3;

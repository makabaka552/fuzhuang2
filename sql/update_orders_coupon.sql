-- 为订单表添加优惠券相关字段
-- 执行此SQL前请确保已备份数据库

USE your_database_name;

-- 添加优惠券ID字段
ALTER TABLE orders ADD COLUMN coupon_id BIGINT NULL COMMENT '使用的优惠券ID';

-- 添加优惠金额字段
ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额';

-- 添加原始订单金额字段
ALTER TABLE orders ADD COLUMN original_amount DECIMAL(10,2) NULL COMMENT '原始订单金额（优惠前）';

-- 为优惠券表添加状态更新索引（如果不存在）
-- ALTER TABLE coupon ADD INDEX idx_status (status);

-- 验证字段是否添加成功
-- SELECT coupon_id, discount_amount, original_amount FROM orders LIMIT 1;

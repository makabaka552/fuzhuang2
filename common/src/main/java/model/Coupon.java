package model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Coupon {
    private Long id;                // 优惠券ID，主键
    private String name;            // 优惠券名称
    private String code;            // 优惠券代码/兑换码
    private BigDecimal discountAmount;  // 优惠金额(减多少元)
    private BigDecimal minOrderAmount;  // 最低订单金额(满多少元可用)
    private Date startDate;         // 优惠券开始生效日期
    private Date endDate;           // 优惠券结束日期
    private Integer status;         // 优惠券状态: 0-未使用 1-已使用 2-已过期
    private Long userId;            // 所属用户ID
    private Date createTime;        // 创建时间
    private Date updateTime;        // 最后更新时间
    // 积分商城相关字段
    private Integer requiredPoints; // 兑换所需积分
    private String description;     // 优惠券描述
    private Integer type;           // 优惠券类型: 0-满减券 1-折扣券 2-无门槛券
    private Integer stock;          // 库存数量
    private Integer totalStock;     // 总库存
    private Integer isActive;       // 是否上架: 0-下架 1-上架
}
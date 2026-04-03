package points.service.impl;

import model.Coupon;
import model.Result;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import points.mapper.CouponMapper;
import points.mapper.UserMapper;
import points.service.CouponService;
import points.service.PointsService;

import java.util.List;
import java.util.UUID;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Override
    @Transactional
    public Result<Coupon> exchangeCoupon(Long userId, Long couponId) {
        // 1. 获取用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. 获取优惠券信息
        Coupon shopCoupon = couponService.getCouponById(couponId);
        if (shopCoupon == null) {
            return Result.error("优惠券不存在");
        }

        // 3. 检查优惠券是否上架
        if (shopCoupon.getIsActive() == null || shopCoupon.getIsActive() != 1) {
            return Result.error("该优惠券已下架");
        }

        // 4. 检查库存
        if (shopCoupon.getStock() == null || shopCoupon.getStock() <= 0) {
            return Result.error("该优惠券已售罄");
        }

        // 5. 检查用户积分是否足够
        Integer requiredPoints = shopCoupon.getRequiredPoints();
        if (user.getPoints() == null || user.getPoints() < requiredPoints) {
            return Result.error("积分不足，无法兑换");
        }

        // 6. 扣除用户积分
        int deducted = userMapper.deductPoints(userId, requiredPoints);
        if (deducted <= 0) {
            return Result.error("积分扣除失败");
        }

        // 7. 减少优惠券库存
        boolean stockDecreased = couponService.decreaseStock(couponId);
        if (!stockDecreased) {
            // 回滚积分
            userMapper.addPoints(userId, requiredPoints);
            return Result.error("库存不足，兑换失败");
        }

        // 8. 为用户创建一张新的优惠券
        Coupon userCoupon = new Coupon();
        userCoupon.setName(shopCoupon.getName());
        userCoupon.setCode(generateCouponCode());
        userCoupon.setDiscountAmount(shopCoupon.getDiscountAmount());
        userCoupon.setMinOrderAmount(shopCoupon.getMinOrderAmount());
        userCoupon.setStartDate(shopCoupon.getStartDate());
        userCoupon.setEndDate(shopCoupon.getEndDate());
        userCoupon.setStatus(0); // 未使用
        userCoupon.setUserId(userId);
        userCoupon.setDescription(shopCoupon.getDescription());
        userCoupon.setType(shopCoupon.getType());
        userCoupon.setIsActive(1);

        couponMapper.save(userCoupon);

        return Result.success("兑换成功",userCoupon );
    }

    @Override
    public List<Coupon> getUserCoupons(Long userId) {
        return couponService.getUserCoupons(userId);
    }

    @Override
    @Transactional
    public boolean useCoupon(Long couponId) {
        try {
            Coupon coupon = couponMapper.findById(couponId).orElse(null);
            if (coupon == null) {
                return false;
            }
            if (coupon.getStatus() != null && coupon.getStatus() != 0) {
                return false;
            }
            coupon.setStatus(1);
            coupon.setIsActive(0);
            couponMapper.updateStatus(couponId, 1);
            couponMapper.updateActiveStatus(couponId, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addPointsToUser(Long userId, Integer points) {
        try {
            if (points == null || points <= 0) {
                return false;
            }
            return userMapper.addPoints(userId, points) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateCouponCode() {
        return "CPN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

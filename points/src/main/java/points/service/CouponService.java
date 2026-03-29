package points.service;

import model.Coupon;
import java.util.List;

public interface CouponService {
    List<Coupon> getAllCoupons();
    List<Coupon> getActiveCoupons();
    Coupon createCoupon(Coupon coupon);
    Coupon getCouponById(Long id);
    Coupon updateCoupon(Long id, Coupon coupon);
    void deleteCoupon(Long id);
    void updateActiveStatus(Long id, Integer isActive);
    boolean decreaseStock(Long id);
    void increaseStock(Long id);
    List<Coupon> getUserCoupons(Long userId);
}
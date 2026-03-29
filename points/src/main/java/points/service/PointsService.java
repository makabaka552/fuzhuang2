package points.service;

import model.Coupon;
import model.Result;
import java.util.List;

public interface PointsService {
    Result<Coupon> exchangeCoupon(Long userId, Long couponId);
    List<Coupon> getUserCoupons(Long userId);
    
    boolean useCoupon(Long couponId);
    boolean addPointsToUser(Long userId, Integer points);
}

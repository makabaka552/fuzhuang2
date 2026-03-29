package points.service.impl;

import model.Coupon;
import points.mapper.CouponMapper;
import points.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<Coupon> getAllCoupons() {
        return couponMapper.findAll();
    }

    @Override
    public List<Coupon> getActiveCoupons() {
        return couponMapper.findActiveCoupons();
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        coupon.setStock(coupon.getTotalStock() != null ? coupon.getTotalStock() : 0);
        coupon.setIsActive(1);
        couponMapper.save(coupon);
        return coupon;
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponMapper.findById(id).orElseThrow(() -> new RuntimeException("优惠券不存在"));
    }

    @Override
    public Coupon updateCoupon(Long id, Coupon coupon) {
        Coupon existingCoupon = getCouponById(id);
        if (coupon.getName() != null) {
            existingCoupon.setName(coupon.getName());
        }
        if (coupon.getDescription() != null) {
            existingCoupon.setDescription(coupon.getDescription());
        }
        if (coupon.getRequiredPoints() != null) {
            existingCoupon.setRequiredPoints(coupon.getRequiredPoints());
        }
        if (coupon.getDiscountAmount() != null) {
            existingCoupon.setDiscountAmount(coupon.getDiscountAmount());
        }
        if (coupon.getMinOrderAmount() != null) {
            existingCoupon.setMinOrderAmount(coupon.getMinOrderAmount());
        }
        if (coupon.getTotalStock() != null) {
            existingCoupon.setTotalStock(coupon.getTotalStock());
            if (coupon.getTotalStock() > existingCoupon.getStock()) {
                existingCoupon.setStock(existingCoupon.getStock() + (coupon.getTotalStock() - existingCoupon.getTotalStock()));
            }
        }
        if (coupon.getStartDate() != null) {
            existingCoupon.setStartDate(coupon.getStartDate());
        }
        if (coupon.getEndDate() != null) {
            existingCoupon.setEndDate(coupon.getEndDate());
        }
        if (coupon.getType() != null) {
            existingCoupon.setType(coupon.getType());
        }
        couponMapper.update(existingCoupon);
        return existingCoupon;
    }

    @Override
    public void deleteCoupon(Long id) {
        couponMapper.deleteById(id);
    }

    @Override
    public void updateActiveStatus(Long id, Integer isActive) {
        couponMapper.updateActiveStatus(id, isActive);
    }

    @Override
    @Transactional
    public boolean decreaseStock(Long id) {
        return couponMapper.decreaseStock(id) > 0;
    }

    @Override
    public void increaseStock(Long id) {
        couponMapper.increaseStock(id);
    }

    @Override
    public List<Coupon> getUserCoupons(Long userId) {
        return couponMapper.findByUserId(userId);
    }
}
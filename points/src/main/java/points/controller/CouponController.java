package points.controller;

import model.Coupon;
import model.Result;
import points.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // ========== 消费者接口 ==========
    
    @GetMapping("/active")
    public Result<List<Coupon>> getActiveCoupons() {
        return Result.success(couponService.getActiveCoupons());
    }

    @GetMapping("/{id}")
    public Result<Coupon> getCouponById(@PathVariable Long id) {
        return Result.success(couponService.getCouponById(id));
    }

    // ========== 管理员接口 ==========

    @GetMapping("/admin")
    public Result<List<Coupon>> getAllCoupons() {
        return Result.success(couponService.getAllCoupons());
    }

    @PostMapping("/admin")
    public Result<Coupon> createCoupon(@RequestBody Coupon coupon) {
        return Result.success(couponService.createCoupon(coupon));
    }

    @PutMapping("/admin/{id}")
    public Result<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return Result.success(couponService.updateCoupon(id, coupon));
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return Result.success(null);
    }

    @PutMapping("/admin/{id}/status")
    public Result<Void> updateActiveStatus(@PathVariable Long id, @RequestParam Integer isActive) {
        couponService.updateActiveStatus(id, isActive);
        return Result.success(null);
    }
}
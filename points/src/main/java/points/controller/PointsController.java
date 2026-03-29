package points.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import model.Coupon;
import model.Result;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import points.mapper.UserMapper;
import points.service.PointsService;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/points")
@Slf4j
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @Autowired
    private UserMapper userMapper;

    // 获取当前用户积分
    @GetMapping("/user")
    public Result<Integer> getUserPoints(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = JwtUtil.parseJWT(token);
            String username = (String) claims.get("username");
            User user = userMapper.findByUsername(username);
            return Result.success(user.getPoints());
        } catch (Exception e) {
            log.error("获取用户积分失败", e);
            return Result.error("获取用户积分失败");
        }
    }

    // 积分兑换优惠券
    @PostMapping("/exchange")
    public Result<Coupon> exchangeCoupon(
            HttpServletRequest request,
            @RequestBody Map<String, Long> requestBody) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = JwtUtil.parseJWT(token);
            String username = (String) claims.get("username");
            User user = userMapper.findByUsername(username);
            
            Long couponId = requestBody.get("couponId");
            return pointsService.exchangeCoupon(user.getId(), couponId);
        } catch (Exception e) {
            log.error("兑换失败", e);
            return Result.error("兑换失败: " + e.getMessage());
        }
    }

    // 获取用户优惠券列表
    @GetMapping("/coupons")
    public Result<List<Coupon>> getUserCoupons(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = JwtUtil.parseJWT(token);
            String username = (String) claims.get("username");
            User user = userMapper.findByUsername(username);
            
            return Result.success(pointsService.getUserCoupons(user.getId()));
        } catch (Exception e) {
            log.error("获取用户优惠券失败", e);
            return Result.error("获取用户优惠券失败");
        }
    }

    // 使用优惠券（内部服务调用）
    @PostMapping("/coupon/use")
    public Result<Boolean> useCoupon(@RequestBody Map<String, Long> requestBody) {
        try {
            Long couponId = requestBody.get("couponId");
            boolean result = pointsService.useCoupon(couponId);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("优惠券使用失败");
            }
        } catch (Exception e) {
            log.error("使用优惠券失败", e);
            return Result.error("使用优惠券失败: " + e.getMessage());
        }
    }

    // 添加积分（内部服务调用）
    @PostMapping("/add")
    public Result<Boolean> addPoints(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            Integer points = Integer.valueOf(requestBody.get("points").toString());
            boolean result = pointsService.addPointsToUser(userId, points);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("添加积分失败");
            }
        } catch (Exception e) {
            log.error("添加积分失败", e);
            return Result.error("添加积分失败: " + e.getMessage());
        }
    }
}

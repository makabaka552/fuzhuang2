package review.controller;

import lombok.extern.slf4j.Slf4j;
import model.Order;
import model.Result;
import model.Review;
import model.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import review.server.ReviewServer;
import utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewServer reviewServer;

    @GetMapping("/product/{productid}")
    public Result FindProductReviews(@PathVariable Long productid){
        List<Review> reviews = reviewServer.FindProductReview(productid);
        return Result.success(reviews);
    }

    @GetMapping("/user")
    public Result FindReviewByUser(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        List<Review> reviews = reviewServer.FindReviewByUser(username);
        return  Result.success(reviews);
    }

    @GetMapping("/pending")
    public Result FindReviewByPending(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        List<Order> reviews = reviewServer.FindReviewByPending(username);
        return  Result.success(reviews);
    }
    @PostMapping("/submit")
    public Result AddReview(HttpServletRequest request,@RequestBody ReviewRequest reviewRequest){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉"Bearer "
        }
        // 2. 解析用户名
        String username = JwtUtil.getUsernameJwt(token);
        reviewServer.AddReview(username,reviewRequest);

        return Result.success();
    }
    @GetMapping("/all")
    public Result getAllReviews() {
        List<Review> reviews = reviewServer.getAllReviews();
        return Result.success(reviews);
    }
    
    @DeleteMapping("/{reviewId}")
    public Result deleteReview(@PathVariable Long reviewId) {
        reviewServer.deleteReview(reviewId);
        return Result.success();
    }


}


























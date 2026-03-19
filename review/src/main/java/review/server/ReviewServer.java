package review.server;

import model.Order;
import model.Review;
import model.ReviewRequest;

import java.util.List;
import java.util.Map;

public interface ReviewServer {
    List<Review> FindReviewByUser(String username);

    List<Order> FindReviewByPending(String username);

    List<Review> FindProductReview(Long productid);

    void AddReview(String username, ReviewRequest reviewRequest);

    /**
     * 更新订单状态
     * 
     * @param orderId   订单ID
     * @param newStatus 新的订单状态
     * @return 更新后的订单
     */
    Order updateOrderStatus(Long orderId, String newStatus);

    List<Review> getAllReviews();

    void deleteReview(Long reviewId);

}

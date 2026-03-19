package review.server.impl;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import review.mapper.OrderMapper;
import review.mapper.ProductMapper;
import review.mapper.ReviewMapper;
import review.mapper.UserMapper;
import review.server.ReviewServer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServerImpl implements ReviewServer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Review> FindReviewByUser(String username) {
        User user = userMapper.findByUsername(username);
        List<Review> reviews = reviewMapper.findAllByUser(user);
        return reviews;
    }

    @Override
    public List<Order> FindReviewByPending(String username) {
        User user = userMapper.findByUsername(username);
        String status = OrderStatus.PENDING_REVIEW.getCode();
        List<Order> orders = orderMapper.findByUserIdAndStatus(user.getId(), status);
        return orders;
    }

    @Override
    public List<Review> FindProductReview(Long productid) {
        Products product = productMapper.findById(productid).orElseThrow(() -> new RuntimeException("商品不存在"));
        List<Review> reviews = reviewMapper.findByproduct(product);
        return reviews;
    }

    @Override
    public void AddReview(String username, ReviewRequest reviewRequest) {
        User user = userMapper.findByUsername(username);
        Order order = orderMapper.findById(reviewRequest.getOrderId()).orElseThrow(() -> new RuntimeException("订单不存在"));

        // 检查订单状态是否为待评价
        if (!OrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            throw new RuntimeException("只有已完成状态的订单才能进行评价");
        }

        Review review = new Review();
        review.setUser(user);
        review.setOrder(order);
        review.setProduct(
                productMapper.findById(reviewRequest.getProductId()).orElseThrow(() -> new RuntimeException("商品不存在")));
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setCreateTime(LocalDateTime.now());
        review.setImageUrl(reviewRequest.getImages());
        review.setAvatar("/src/assets/images/2.jpg");
        reviewMapper.save(review);

        // 评价后更新订单状态为已评价
        order.setStatus(OrderStatus.REVIEWED.getCode());
        orderMapper.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) {
        // 验证状态是否为有效的枚举值
        OrderStatus status = OrderStatus.fromCode(newStatus);
        if (status == null) {
            throw new RuntimeException("无效的订单状态");
        }

        // 获取订单
        Order order = orderMapper.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));

        // 验证状态流转是否合法
        validateStatusTransition(order.getStatus(), newStatus);

        // 更新状态
        order.setStatus(newStatus);
        orderMapper.save(order);

        log.info("订单 {} 状态已更新为: {}", orderId, status.getDescription());
        return order;
    }

    /**
     * 验证订单状态流转是否合法
     * 
     * @param currentStatus 当前状态
     * @param newStatus     新状态
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // 只有已完成状态才能变为待评价
        if (newStatus.equals(OrderStatus.PENDING_REVIEW.getCode())
                && !currentStatus.equals(OrderStatus.COMPLETED.getCode())) {
            throw new RuntimeException("只有已完成的订单才能变更为待评价状态");
        }

        // 只有待评价状态才能变为已评价
        if (newStatus.equals(OrderStatus.REVIEWED.getCode())
                && !currentStatus.equals(OrderStatus.PENDING_REVIEW.getCode())) {
            throw new RuntimeException("只有待评价的订单才能变更为已评价状态");
        }

        // 可以根据业务需求添加更多状态流转的验证规则
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewMapper.findAll();
    }
    
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewMapper.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        reviewMapper.delete(review);
    }


}

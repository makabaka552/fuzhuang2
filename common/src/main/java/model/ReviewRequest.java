package model;

import java.util.List;

public class ReviewRequest {
    private Long orderId;      // 订单ID（必须）
    private Long productId;    // 产品ID（必须）
    private Integer rating;    // 评分（1-5星）
    private String content;   // 评价内容
    private String images; // 图片URL列表（可选）

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
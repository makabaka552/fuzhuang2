package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer image_id;

    @Column(name = "product_id")
    private Long product_id;

    @Column(name = "color_id")
    private Integer color_id;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "is_primary")
    private Boolean is_primary;

    @Column(name = "display_order")
    private Integer display_order;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Products product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "color_id", insertable = false, updatable = false)
    private Color color;

    // Getters and Setters
    public Integer getImageId() {
        return image_id;
    }

    public void setImageId(Integer image_id) {
        this.image_id = image_id;
    }

    public Long getProductId() {
        return product_id;
    }

    public void setProductId(Long productId) {
        this.product_id = product_id;
    }

    public Integer getColorId() {
        return color_id;
    }

    public void setColorId(Integer color_id) {
        this.color_id = color_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    // 为了向后兼容，保留旧的getter和setter
    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public Boolean getIsPrimary() {
        return is_primary;
    }

    public void setIsPrimary(Boolean is_primary) {
        this.is_primary = is_primary;
    }

    public Integer getDisplayOrder() {
        return display_order;
    }

    public void setDisplayOrder(Integer display_order) {
        this.display_order = display_order;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
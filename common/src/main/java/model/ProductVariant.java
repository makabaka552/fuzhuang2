package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Integer variant_id;

    @Column(name = "product_id")
    private Long product_id;

    @Column(name = "color_id")
    private Integer color_id;

    @Column(name = "size_id")
    private Integer size_id;

    @Column(name = "sku_code")
    private String sku_code;

    @Column(name = "stock_quantity")
    private Integer stock_quantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Products product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "color_id", insertable = false, updatable = false)
    private Color color;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "size_id", insertable = false, updatable = false)
    private Size size;

    // Getters and Setters
    public Integer get_variant_id() {
        return variant_id;
    }

    public void set_variant_id(Integer variant_id) {
        this.variant_id = variant_id;
    }

    public Long get_product_id() {
        return product_id;
    }

    public void set_product_id(Long product_id) {
        this.product_id = product_id;
    }

    public Integer get_color_id() {
        return color_id;
    }

    public void set_color_id(Integer color_id) {
        this.color_id = color_id;
    }

    public Integer get_size_id() {
        return size_id;
    }

    public void set_size_id(Integer size_id) {
        this.size_id = size_id;
    }

    public String get_sku_code() {
        return sku_code;
    }

    public void set_sku_code(String sku_code) {
        this.sku_code = sku_code;
    }

    public Integer get_stock_quantity() {
        return stock_quantity;
    }

    public void set_stock_quantity(Integer stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public BigDecimal get_price() {
        return price;
    }

    public void set_price(BigDecimal price) {
        this.price = price;
    }

    public String get_image_url() {
        return image_url;
    }

    public void set_image_url(String image_url) {
        this.image_url = image_url;
    }

    public Integer get_status() {
        return status;
    }

    public void set_status(Integer status) {
        this.status = status;
    }

    public LocalDateTime get_created_at() {
        return created_at;
    }

    public void set_created_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime get_updated_at() {
        return updated_at;
    }

    public void set_updated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Products get_product() {
        return product;
    }

    public void set_product(Products product) {
        this.product = product;
    }

    public Color get_color() {
        return color;
    }

    public void set_color(Color color) {
        this.color = color;
    }

    public Size get_size() {
        return size;
    }

    public void set_size(Size size) {
        this.size = size;
    }

    // 为了保持兼容性，保留原来的getter和setter方法
    public Integer getVariantId() {
        return get_variant_id();
    }

    public void setVariantId(Integer variant_id) {
        set_variant_id(variant_id);
    }

    public Long getProductId() {
        return get_product_id();
    }

    public void setProductId(Long product_id) {
        set_product_id(product_id);
    }

    public Integer getColorId() {
        return get_color_id();
    }

    public void setColorId(Integer color_id) {
        set_color_id(color_id);
    }

    public Integer getSizeId() {
        return get_size_id();
    }

    public void setSizeId(Integer size_id) {
        set_size_id(size_id);
    }

    public String getSkuCode() {
        return get_sku_code();
    }

    public void setSkuCode(String sku_code) {
        set_sku_code(sku_code);
    }

    public Integer getStockQuantity() {
        return get_stock_quantity();
    }

    public void setStockQuantity(Integer stock_quantity) {
        set_stock_quantity(stock_quantity);
    }

    public String getImageUrl() {
        return get_image_url();
    }

    public void setImageUrl(String image_url) {
        set_image_url(image_url);
    }

    public Integer getStatus() {
        return get_status();
    }

    public void setStatus(Integer status) {
        set_status(status);
    }

    public LocalDateTime getCreatedAt() {
        return get_created_at();
    }

    public void setCreatedAt(LocalDateTime created_at) {
        set_created_at(created_at);
    }

    public LocalDateTime getUpdatedAt() {
        return get_updated_at();
    }

    public void setUpdatedAt(LocalDateTime updated_at) {
        set_updated_at(updated_at);
    }

    public Products getProduct() {
        return get_product();
    }

    public void setProduct(Products product) {
        set_product(product);
    }

    public Color getColor() {
        return get_color();
    }

    public void setColor(Color color) {
        set_color(color);
    }

    public Size getSize() {
        return get_size();
    }

    public void setSize(Size size) {
        set_size(size);
    }
}
package model;

import java.math.BigDecimal;

public class ProductVariantDTO {
    private Integer variant_id;
    private Long product_id;
    private Integer color_id;
    private String color_name;
    private String color_code;
    private Integer size_id;
    private String size_name;
    private String size_code;
    private String size_value;
    private String sku_code;
    private Integer stock_quantity;
    private BigDecimal price;
    private String image_url;
    private Integer status;

    // Getters and Setters - 下划线风格
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

    public String get_color_name() {
        return color_name;
    }

    public void set_color_name(String color_name) {
        this.color_name = color_name;
    }

    public String get_color_code() {
        return color_code;
    }

    public void set_color_code(String color_code) {
        this.color_code = color_code;
    }

    public Integer get_size_id() {
        return size_id;
    }

    public void set_size_id(Integer size_id) {
        this.size_id = size_id;
    }

    public String get_size_name() {
        return size_name;
    }

    public void set_size_name(String size_name) {
        this.size_name = size_name;
    }

    public String get_size_code() {
        return size_code;
    }

    public void set_size_code(String size_code) {
        this.size_code = size_code;
    }

    public String get_size_value() {
        return size_value;
    }

    public void set_size_value(String size_value) {
        this.size_value = size_value;
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

    public String getColorName() {
        return get_color_name();
    }

    public void setColorName(String color_name) {
        set_color_name(color_name);
    }

    public String getColorCode() {
        return get_color_code();
    }

    public void setColorCode(String color_code) {
        set_color_code(color_code);
    }

    public Integer getSizeId() {
        return get_size_id();
    }

    public void setSizeId(Integer size_id) {
        set_size_id(size_id);
    }

    public String getSizeName() {
        return get_size_name();
    }

    public void setSizeName(String size_name) {
        set_size_name(size_name);
    }

    public String getSizeCode() {
        return get_size_code();
    }

    public void setSizeCode(String size_code) {
        set_size_code(size_code);
    }

    public String getSizeValue() {
        return get_size_value();
    }

    public void setSizeValue(String size_value) {
        set_size_value(size_value);
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

    public BigDecimal getPrice() {
        return get_price();
    }

    public void setPrice(BigDecimal price) {
        set_price(price);
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
}
package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id")
    private Long category_id;

    private String name;
    private BigDecimal price;

    @Column(name = "image_url")
    private String image_url;

    private Integer stock;
    private Integer status;
    private String category;

    @Column(name = "sub_category")
    private String sub_category;

    @Column(name = "created_time")
    private LocalDateTime created_time;

    @Column(name = "updated_time")
    private LocalDateTime updated_time;

    @Column(name = "brand_id")
    private Integer brand_id;

    @Column(name = "size_category_id")
    private Integer size_category_id;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<ProductVariant> variants;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private Brand brand;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "size_category_id", insertable = false, updatable = false)
    private SizeCategory size_category;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return category_id;
    }

    public void setCategoryId(Long category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return sub_category;
    }

    public void setSubCategory(String sub_category) {
        this.sub_category = sub_category;
    }

    public LocalDateTime getCreatedTime() {
        return created_time;
    }

    public void setCreatedTime(LocalDateTime created_time) {
        this.created_time = created_time;
    }

    public LocalDateTime getUpdatedTime() {
        return updated_time;
    }

    public void setUpdatedTime(LocalDateTime updated_time) {
        this.updated_time = updated_time;
    }

    public Integer getBrandId() {
        return brand_id;
    }

    public void setBrandId(Integer brand_id) {
        this.brand_id = brand_id;
    }

    public Integer getSizeCategoryId() {
        return size_category_id;
    }

    public void setSizeCategoryId(Integer size_category_id) {
        this.size_category_id = size_category_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public SizeCategory getSizeCategory() {
        return size_category;
    }

    public void setSizeCategory(SizeCategory size_category) {
        this.size_category = size_category;
    }

    // toString() 方法
    @Override
    public String toString() {
        return "Products{" +
                "id=" + id +
                ", categoryId=" + category_id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", image_url='" + image_url + '\'' +
                ", stock=" + stock +
                ", status=" + status +
                ", category='" + category + '\'' +
                ", subCategory='" + sub_category + '\'' +
                ", brandId=" + brand_id +
                ", sizeCategoryId=" + size_category_id +
                ", description='" + description + '\'' +
                ", createdTime=" + created_time +
                ", updatedTime=" + updated_time +
                '}';
    }

    // 构造方法
    public Products() {
    }
}

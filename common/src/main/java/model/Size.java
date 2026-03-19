package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

@Entity
@Table(name = "sizes")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Integer size_id;

    @Column(name = "size_category_id")
    private Integer size_category_id;

    @Column(name = "size_name")
    private String size_name;

    @Column(name = "size_code")
    private String size_code;

    @Column(name = "size_value")
    private String size_value;

    @Column(name = "size_order")
    private Integer size_order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "size_category_id", insertable = false, updatable = false)
    private SizeCategory sizeCategory;

    // Getters and Setters
    public Integer getSize_id() {
        return size_id;
    }

    public void setSize_id(Integer size_id) {
        this.size_id = size_id;
    }

    public Integer getSize_category_id() {
        return size_category_id;
    }

    public void setSize_category_id(Integer size_category_id) {
        this.size_category_id = size_category_id;
    }

    public String getSize_name() {
        return size_name;
    }

    public void setSize_name(String size_name) {
        this.size_name = size_name;
    }

    public String getSize_code() {
        return size_code;
    }

    public void setSize_code(String size_code) {
        this.size_code = size_code;
    }

    public String getSize_value() {
        return size_value;
    }

    public void setSize_value(String size_value) {
        this.size_value = size_value;
    }

    public Integer getSize_order() {
        return size_order;
    }

    public void setSize_order(Integer size_order) {
        this.size_order = size_order;
    }

    public SizeCategory getSizeCategory() {
        return sizeCategory;
    }

    public void setSizeCategory(SizeCategory sizeCategory) {
        this.sizeCategory = sizeCategory;
    }

    // 兼容旧代码的方法
    public Integer getSizeId() {
        return getSize_id();
    }

    // 添加兼容的setter方法，解决MyBatis映射错误
    public void setSizeId(Integer sizeId) {
        this.size_id = sizeId;
    }

    public String getSizeName() {
        return getSize_name();
    }

    public void setSizeName(String sizeName) {
        this.size_name = sizeName;
    }

    public String getSizeCode() {
        return getSize_code();
    }

    public void setSizeCode(String sizeCode) {
        this.size_code = sizeCode;
    }

    public String getSizeValue() {
        return getSize_value();
    }

    public void setSizeValue(String sizeValue) {
        this.size_value = sizeValue;
    }

    public Integer getSizeCategoryId() {
        return getSize_category_id();
    }

    public void setSizeCategoryId(Integer sizeCategoryId) {
        this.size_category_id = sizeCategoryId;
    }

    public Integer getSizeOrder() {
        return getSize_order();
    }

    public void setSizeOrder(Integer sizeOrder) {
        this.size_order = sizeOrder;
    }
}
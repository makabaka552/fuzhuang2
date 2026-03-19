package model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "size_categories")
public class SizeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "sizeCategory")
    private List<Size> sizes;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    // 添加使用下划线命名的兼容方法
    public Integer get_id() {
        return getId();
    }

    public void set_id(Integer id) {
        setId(id);
    }

    public String get_name() {
        return getName();
    }

    public void set_name(String name) {
        setName(name);
    }

    public String get_description() {
        return getDescription();
    }

    public void set_description(String description) {
        setDescription(description);
    }
}
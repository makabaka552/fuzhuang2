package model;

import javax.persistence.*;

@Entity
@Table(name = "colors")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "color_name")
    private String color_name;

    @Column(name = "color_code")
    private String color_code;

    // 下划线风格的Getters和Setters
    public Integer get_id() {
        return id;
    }

    public void set_id(Integer id) {
        this.id = id;
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

    // 兼容方法 - 保留原来的getter和setter
    public Integer getId() {
        return get_id();
    }

    public void setId(Integer id) {
        set_id(id);
    }

    public String getName() {
        return get_color_name();
    }

    public void setName(String color_name) {
        set_color_name(color_name);
    }

    public String getCode() {
        return get_color_code();
    }

    public void setCode(String color_code) {
        set_color_code(color_code);
    }
}

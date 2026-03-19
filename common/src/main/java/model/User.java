package model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;
    private String password;
    private String username;
    private String role = "USER";
    private String phone;


    @JsonIgnore
    public String getPassword() {
        return password;
    }
    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public  String getPhone(){ return phone;}

    public void setPhone(String phone) {
        this.phone = phone;
    }




    @Override
    public String toString() {
        return "User{" +
                "uaccount='" + id + '\'' +
                ", upassword='" + password + '\'' +
                ", uname='" + username + '\'' +
                ", usex='" + role + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public User() {
    }

    public User(Long id, String password, String username, String role, String phone) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.role = role;
        this.phone = phone;
    }
}

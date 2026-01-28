package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;
    @JsonIgnore // (Để khi trả về JSON sẽ ẩn mật khẩu đi)
    private String password; // Lưu mật khẩu đã mã hóa
    private String email;
    private String fullName;

    // Quan hệ: 1 User có nhiều Quyền (Ví dụ vừa là Admin vừa là User)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", // Tên bảng phụ trung gian
            joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
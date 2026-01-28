package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity { // Kế thừa BaseEntity lấy ID

    private String name; // Ví dụ: "ADMIN", "USER"
}
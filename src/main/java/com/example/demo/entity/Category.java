package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Quan hệ 1-N: Một danh mục có nhiều sản phẩm
    // mappedBy phải trùng tên với biến category bên file Product
    @OneToMany(mappedBy = "category")
    @JsonIgnore // Ngắt vòng lặp vô tận khi chuyển thành JSON
    private List<Product> products;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
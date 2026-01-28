package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products") // Đặt tên bảng số nhiều cho chuẩn
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true) // Quan trọng: Để Lombok hiểu cả fields của lớp cha (BaseEntity)
public class Product extends BaseEntity { // <--- 1. KẾ THỪA BASE ENTITY

    // LƯU Ý: Đã XÓA phần khai báo ID vì BaseEntity đã có rồi.

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;

    private Integer quantity;

    private String image; // Link ảnh sản phẩm

    // --- CÁC TRƯỜNG ĐẶC THÙ GUNDAM/FIGURE (Theo Slide Box8) ---

    private String series; // VD: One Piece, Gundam SEED

    private String scale; // VD: 1/144, 1/7

    private String manufacturer; // VD: Bandai, GoodSmile

    private String status; // VD: Pre-order, Sẵn hàng

    // --- QUAN HỆ ---
    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;
}
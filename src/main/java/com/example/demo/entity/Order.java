package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

    private LocalDateTime orderDate = LocalDateTime.now(); // Ngày đặt

    private Double totalPrice; // Tổng tiền

    private String status; // Trạng thái: "Đang xử lý", "Đã giao"...
    private String address; // Địa chỉ giao hàng
    private String phone;
    // Quan hệ: Đơn hàng này của ai?
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
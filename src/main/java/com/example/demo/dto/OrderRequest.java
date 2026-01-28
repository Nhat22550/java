package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private String address;
    private String phone;

    // 👇 THAY ĐỔI: Nhận danh sách nhiều món thay vì 1 món lẻ
    private List<CartItemDto> cartItems;

    // Thêm trường này để chọn phương thức thanh toán
    // 0: Tiền mặt (COD), 1: Online (VNPay)
    private Integer paymentMethod;
}
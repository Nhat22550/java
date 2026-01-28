package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    // [cite: 62]
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Bổ sung thêm field này để hỗ trợ bài toán Category (nếu bạn đã làm phần
    // --- THÊM CÁC TRƯỜNG GUNDAM (Mới) ---
    private String series; // VD: One Piece
    private String scale; // VD: 1/144
    private String manufacturer; // VD: Bandai
    private String status; // VD: Sẵn hàng
    // Category)
    private Long categoryId;
    private String categoryName;
}
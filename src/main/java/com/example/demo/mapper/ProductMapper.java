package com.example.demo.mapper;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;

public class ProductMapper {

    // 1. Sửa hàm toDto (Để Frontend NHẬN được ảnh)
    public static ProductDto toDto(Product p) {
        if (p == null)
            return null;

        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())

                // 👉 THÊM DÒNG NÀY:
                .image(p.getImage())

                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .build();
    }

    // 2. Sửa hàm toEntity (Để lưu ảnh MỚI vào Database)
    public static Product toEntity(ProductDto d) {
        if (d == null)
            return null;

        Product product = Product.builder()
                .name(d.getName())
                .description(d.getDescription())
                .price(d.getPrice())
                .quantity(d.getQuantity())

                // 👉 THÊM DÒNG NÀY:
                .image(d.getImage())

                .build();

        product.setId(d.getId());
        return product;
    }
}
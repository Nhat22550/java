package com.example.demo.mapper;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;

public class ProductMapper {

    // 1. Sửa hàm toDto (Để Frontend NHẬN được Series & Hãng SX)
    public static ProductDto toDto(Product p) {
        if (p == null)
            return null;

        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .image(p.getImage())

                // 👇 THÊM 2 DÒNG NÀY:
                .series(p.getSeries()) // Chép Series
                .manufacturer(p.getManufacturer()) // Chép Hãng SX

                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .build();
    }

    // 2. Sửa hàm toEntity (Để lưu Series & Hãng SX MỚI vào Database)
    public static Product toEntity(ProductDto d) {
        if (d == null)
            return null;

        Product product = Product.builder()
                .name(d.getName())
                .description(d.getDescription())
                .price(d.getPrice())
                .quantity(d.getQuantity())
                .image(d.getImage())

                // 👇 THÊM 2 DÒNG NÀY:
                .series(d.getSeries()) // Lưu Series
                .manufacturer(d.getManufacturer()) // Lưu Hãng SX

                .build();

        product.setId(d.getId());
        return product;
    }
}
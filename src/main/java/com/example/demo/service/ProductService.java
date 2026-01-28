package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import java.util.List;

public interface ProductService {

    // Thêm mới
    ProductDto addProduct(ProductDto productDto);

    // Lấy tất cả
    List<ProductDto> getAllProducts();

    // 👉 SỬA: Integer -> Long (Để khớp với DB và Repository chuẩn)
    ProductDto getProductById(Long id);

    // 👉 SỬA: Integer -> Long
    ProductDto updateProduct(Long id, ProductDto productDto);

    // 👉 SỬA: Integer -> Long
    void deleteProduct(Long id);

    // Tìm kiếm
    List<ProductDto> searchByName(String keyword);

    // Tìm theo khoảng giá
    List<ProductDto> searchByPriceRange(Double min, Double max);

    // Thống kê (trả về Object[] là đúng rồi)
    List<Object[]> countProductsByCategory();

    // Tìm sản phẩm doanh thu cao
    List<ProductDto> findHighRevenueProducts(Double minRevenue);
}
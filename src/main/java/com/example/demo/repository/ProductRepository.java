package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // --- 1. CÁC HÀM CŨ (ĐỂ FIX LỖI SERVICE IMPL) ---

    // Tìm theo tên (Dùng cho hàm searchByName)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Tìm theo khoảng giá (Dùng cho hàm searchByPriceRange)
    List<Product> findByPriceBetween(Double min, Double max);

    // Thống kê cũ (Group By Category)
    @Query("SELECT p.category.name, COUNT(p) FROM Product p GROUP BY p.category.name")
    List<Object[]> countProductsByCategory();

    // Tìm doanh thu cao cũ
    @Query("SELECT p FROM Product p WHERE p.price > :revenueThreshold")
    List<Product> findHighRevenueProducts(@Param("revenueThreshold") Double revenueThreshold);

    // --- 2. CÁC HÀM MỚI (CHO ĐỀ TÀI GUNDAM/FIGURE) ---

    // Tìm theo Series phim (VD: One Piece)
    List<Product> findBySeriesContainingIgnoreCase(String series);

    // Tìm theo Hãng sản xuất (VD: Bandai)
    List<Product> findByManufacturerContainingIgnoreCase(String manufacturer);

    // Tìm theo trạng thái (Pre-order/Sẵn hàng)
    List<Product> findByStatus(String status);

    // Thống kê mới: Hãng nào có nhiều sản phẩm nhất?
    @Query("SELECT p.manufacturer, COUNT(p) FROM Product p GROUP BY p.manufacturer")
    List<Object[]> countProductsByManufacturer();
}
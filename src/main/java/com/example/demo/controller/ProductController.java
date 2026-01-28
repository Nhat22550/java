package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.service.ProductService;
import org.springframework.hateoas.EntityModel; // Import cho HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import tạo link
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- CÁC HÀM CƠ BẢN (GIỮ NGUYÊN) ---

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.addProduct(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- 1. SỬA LẠI HÀM NÀY ĐỂ DÙNG HATEOAS ---
    @GetMapping("/{id}")
    public EntityModel<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);

        // Bọc dữ liệu trong EntityModel
        EntityModel<ProductDto> resource = EntityModel.of(product);

        // Tạo link "self" (trỏ về chính nó)
        resource.add(linkTo(methodOn(this.getClass()).getProductById(id)).withSelfRel());

        // Tạo link gợi ý xem danh sách (all-products)
        resource.add(linkTo(methodOn(this.getClass()).getAllProducts()).withRel("all-products"));

        return resource;
    }

    // --- 2. CẬP NHẬT HÀM SEARCH ĐỂ LỌC NÂNG CAO (Giá + Tên) ---
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        // Nếu có khoảng giá thì gọi hàm lọc giá (Basic Query)
        if (minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(productService.searchByPriceRange(minPrice, maxPrice));
        }
        // Mặc định tìm theo tên
        return ResponseEntity.ok(productService.searchByName(keyword != null ? keyword : ""));
    }

    // --- 3. THÊM API CHO TRUY VẤN NÂNG CAO (Thống kê & Tính toán) ---

    // Thống kê số lượng sản phẩm theo danh mục (Advanced Query: Group By)
    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getProductStats() {
        return ResponseEntity.ok(productService.countProductsByCategory());
    }

    // Tìm sản phẩm doanh thu cao > 100 triệu (Advanced Query: Tính toán)
    @GetMapping("/high-revenue")
    public ResponseEntity<List<ProductDto>> getHighRevenue() {
        // Giả sử mốc doanh thu là 100,000,000
        return ResponseEntity.ok(productService.findHighRevenueProducts(100000000.0));
    }
}
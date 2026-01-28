package com.example.demo; // Hoặc package com.example.demo.service; tùy bạn

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException; // Import cái vừa tạo
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductServiceImpl; // Import ServiceImpl
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito
public class ProductServiceTest {

    @Mock // Giả lập Repository (không gọi DB thật)
    private ProductRepository productRepository;

    @InjectMocks // Tiêm Repository giả vào Service thật
    private ProductServiceImpl productService;

    // TEST CASE 1: Lấy sản phẩm thành công
    @Test
    void testGetProductById_Success() {
        // 1. Giả lập dữ liệu trả về từ DB
        Product mockProduct = new Product();
        mockProduct.setId(1L); // ⚠️ QUAN TRỌNG: Phải có chữ L (Long)
        mockProduct.setName("RG God Gundam");
        mockProduct.setPrice(850000.0);
        mockProduct.setSeries("G Gundam");
        mockProduct.setStatus("Sẵn hàng");

        // Dạy cho Mockito biết: "Nếu ai hỏi tìm ID 1L, hãy trả về mockProduct"
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // 2. Gọi hàm cần test
        ProductDto result = productService.getProductById(1L);

        // 3. So sánh
        Assertions.assertEquals("RG God Gundam", result.getName());
        Assertions.assertEquals(850000.0, result.getPrice());
    }

    // TEST CASE 2: Tìm sản phẩm không tồn tại (Biên)
    @Test
    void testGetProductById_NotFound() {
        // 1. Giả lập DB không tìm thấy ID 999
        Mockito.when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // 2. Kiểm tra xem hàm có ném ra lỗi ResourceNotFoundException không
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L); // ⚠️ Nhớ thêm chữ L
        });
    }
}
package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        Product entity = ProductMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(entity);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    // 👉 ĐÃ SỬA: Integer -> Long
    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id: " + id));
        return ProductMapper.toDto(product);
    }

    // 👉 ĐÃ SỬA: Integer -> Long
    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id: " + id));

        // Cập nhật thông tin
        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());

        // 👉 QUAN TRỌNG: Phải cập nhật cả ảnh nữa
        existingProduct.setImage(productDto.getImage());

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDto(updatedProduct);
    }

    // 👉 ĐÃ SỬA: Integer -> Long
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchByPriceRange(Double min, Double max) {
        return productRepository.findByPriceBetween(min, max).stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> countProductsByCategory() {
        return productRepository.countProductsByCategory();
    }

    @Override
    public List<ProductDto> findHighRevenueProducts(Double minRevenue) {
        return productRepository.findHighRevenueProducts(minRevenue).stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
}
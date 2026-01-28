package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Lấy tất cả
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 2. Lấy chi tiết 1 cái
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // 3. Thêm mới hoặc Cập nhật
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 4. Xóa
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
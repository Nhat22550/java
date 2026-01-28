package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data; // ⚠️ Quan trọng: Cái này tạo ra getId() và setId() tự động
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Data // <--- BẮT BUỘC PHẢI CÓ ĐỂ SINH RA GETTER/SETTER CHO ID
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
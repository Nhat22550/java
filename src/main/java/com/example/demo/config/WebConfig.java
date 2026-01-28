package com.example.demo.config; // Nhớ đổi package cho đúng với project của bạn

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Áp dụng cho tất cả API bắt đầu bằng /api/
                .allowedOrigins("http://localhost:3000") // Chỉ cho phép React chạy ở port 3000 gọi vào
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Các method được phép
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
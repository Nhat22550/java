package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép React gọi
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. API Đặt hàng (Ai đăng nhập rồi cũng mua được)
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Order newOrder = orderService.placeOrder(orderRequest);
            return ResponseEntity.ok(newOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. API Xem tất cả đơn hàng (Chỉ Admin)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Autowired
    private com.example.demo.repository.OrderRepository orderRepository; // Nhớ dòng này nếu chưa có

    // 👇 API MỚI: Dùng để cập nhật trạng thái đơn hàng thành công
    @GetMapping("/confirm-payment/{orderId}")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus("Đã thanh toán (VNPay)");
            orderRepository.save(order);
            return ResponseEntity.ok("Cập nhật thành công!");
        }
        return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
    }
}
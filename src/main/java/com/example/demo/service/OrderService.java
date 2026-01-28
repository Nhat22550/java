package com.example.demo.service;

import com.example.demo.dto.CartItemDto;
import com.example.demo.dto.OrderRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. Lấy tất cả đơn hàng (Cho Admin)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. Đặt hàng (Xử lý Giỏ hàng nhiều món)
    @Transactional
    public Order placeOrder(OrderRequest request) {

        // B1. Tìm User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // B2. Tạo Đơn hàng (Order)
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());

        // Xét trạng thái dựa trên phương thức thanh toán
        if (request.getPaymentMethod() != null && request.getPaymentMethod() == 1) {
            order.setStatus("Chờ thanh toán (VNPay)");
        } else {
            order.setStatus("Chờ duyệt"); // Mặc định là Tiền mặt (COD)
        }

        // Lưu đơn hàng để có ID trước
        Order savedOrder = orderRepository.save(order);

        double totalAmount = 0;

        // B3. Duyệt qua từng món trong giỏ hàng
        // (Lưu ý: request.getCartItems() không được null)
        if (request.getCartItems() != null) {
            for (CartItemDto itemDto : request.getCartItems()) {

                // Tìm sản phẩm
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(
                                () -> new RuntimeException("Sản phẩm ID " + itemDto.getProductId() + " không tồn tại"));

                // Kiểm tra kho
                if (product.getQuantity() < itemDto.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng!");
                }

                // Tạo chi tiết đơn hàng
                OrderDetail detail = new OrderDetail();
                detail.setOrder(savedOrder);
                detail.setProduct(product);
                detail.setQuantity(itemDto.getQuantity());
                detail.setPrice(product.getPrice());

                orderDetailRepository.save(detail);

                // Cộng tiền
                totalAmount += (product.getPrice() * itemDto.getQuantity());

                // Trừ kho
                product.setQuantity(product.getQuantity() - itemDto.getQuantity());
                productRepository.save(product);
            }
        }

        // B4. Cập nhật tổng tiền
        savedOrder.setTotalPrice(totalAmount);
        return orderRepository.save(savedOrder);
    }
}
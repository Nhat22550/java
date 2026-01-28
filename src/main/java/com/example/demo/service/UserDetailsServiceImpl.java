package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm user trong DB
        User user = userRepository.findByUsername(username);

        // 2. Kiểm tra Null ngay lập tức (để tránh lỗi nếu nhập sai tên)
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // 3. 👉 IN LOG KIỂM TRA (Code debug an toàn)
        System.out.println("--------------------------------------------------");
        System.out.println("DEBUG LOGIN: User đang đăng nhập là: " + user.getUsername());
        System.out.println("DEBUG LOGIN: Quyền gốc trong DB: " + user.getRoles());

        // 4. Lấy danh sách quyền (Chỉ lấy tên, KHÔNG thêm "ROLE_")
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        System.out.println("DEBUG LOGIN: Quyền nạp vào Security: " + authorities);
        System.out.println("--------------------------------------------------");

        // 5. Trả về UserDetails cho Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}
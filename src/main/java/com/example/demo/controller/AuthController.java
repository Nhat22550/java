package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtTokenProvider; // ⚠️ Nhớ Import file này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 👉 1. TIÊM THÊM CÁI MÁY TẠO TOKEN VÀO ĐÂY
    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // 1. Xác thực user/pass
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // 2. Nếu đúng, lưu vào Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. 👉 TẠO TOKEN TỪ USERNAME (Đây là bước bạn bị thiếu)
            String jwt = tokenProvider.generateToken(request.getUsername());

            // 4. Lấy thông tin user
            User user = userRepository.findByUsername(request.getUsername());

            // 5. 👉 ĐÓNG GÓI TOKEN VÀO HỘP QUÀ ĐỂ GỬI VỀ CHO REACT
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt); // React sẽ tìm cái chữ "accessToken" này
            response.put("tokenType", "Bearer");
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles());
            response.put("token", jwt);
            response.put("user", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu");
        }
    }
}
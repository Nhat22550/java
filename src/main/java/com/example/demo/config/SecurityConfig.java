package com.example.demo.config;

import com.example.demo.filter.JwtAuthenticationFilter; // ⚠️ Nhớ Import đúng đường dẫn file Filter của bạn
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 👉 1. TIÊM (INJECT) FILTER JWT VÀO ĐÂY
    // (Spring sẽ tự tìm file JwtAuthenticationFilter bạn đã viết để nhúng vào)
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 👉 2. QUAN TRỌNG: Cho phép lệnh OPTIONS (Preflight) đi qua
                        // (Đây là chìa khóa để sửa lỗi React không Xóa/Sửa được)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers("/api/products/search/**").permitAll()

                        // Phân quyền Admin
                        // Cho phép xem danh mục thoải mái (Khách cũng xem được)
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        // --- QUẢN LÝ USER (MỚI) ---
                        // Chỉ ADMIN mới được xem và xóa User
                        .requestMatchers("/api/users/**").hasAuthority("ADMIN")
                        // cập nhật đơn hàng
                        .requestMatchers("/api/orders/confirm-payment/**").permitAll() // Cho phép cập nhật đơn
                        // --- QUẢN LÝ ĐƠN HÀNG (MỚI) ---
                        // 1. Cho phép User đã đăng nhập được Đặt hàng
                        .requestMatchers("/api/orders/place").authenticated()

                        // 2. Chỉ Admin mới được xem danh sách đơn hàng
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAuthority("ADMIN")
                        // Chỉ ADMIN mới được Thêm/Sửa/Xóa danh mục
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasAuthority("ADMIN")
                        // product
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ADMIN")
                        //
                        .requestMatchers("/api/payment/**").permitAll() // Cho phép tạo link thanh toán
                        .anyRequest().authenticated())
                // 👉 3. Tắt Session (Vì dùng Token nên không cần lưu Session)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 👉 4. Chèn Filter JWT vào trước Filter xác thực gốc của Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
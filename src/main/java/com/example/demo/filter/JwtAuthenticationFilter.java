package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

// Import class tiện ích JWT
import com.example.demo.util.JwtTokenProvider;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy token từ header gửi lên
            String token = getTokenFromRequest(request);

            // 2. Kiểm tra xem token có hợp lệ không
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                // 3. Lấy username từ trong token ra
                String username = jwtTokenProvider.getUsernameFromJWT(token);

                // 4. Lấy thông tin chi tiết (kèm Role) từ Database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 👉👉👉 DÒNG DEBUG QUAN TRỌNG ĐÃ ĐƯỢC THÊM Ở ĐÂY:
                System.out.println("--------------------------------------------------");
                System.out.println("DEBUG FILTER: User [" + username + "] đang đi qua cửa an ninh.");
                System.out.println("DEBUG FILTER: Quyền thực tế lấy từ Database: " + userDetails.getAuthorities());
                System.out.println("--------------------------------------------------");

                // 5. Nếu user ngon lành, nạp quyền vào Security Context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Không thể xác thực user từ token", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
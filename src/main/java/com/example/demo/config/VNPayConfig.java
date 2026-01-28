package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class VNPayConfig {
    // 1. Cấu hình Sandbox (Môi trường test)
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:3000/payment-result"; // Khi thanh toán xong trả về đây

    // ⚠️ QUAN TRỌNG: 2 Mã này bạn phải đăng ký tài khoản Sandbox mới có.
    // Tạm thời dùng mã mẫu này, nếu không chạy thì bạn vào:
    // https://sandbox.vnpayment.vn/devreg/ để đăng ký nhé (miễn phí)
    public static String vnp_TmnCode = "CQ0JGRS1"; // Mã định danh merchant (Thay của bạn vào)
    public static String vnp_HashSecret = "8LOT3UDSK3TTHL7ZHT5Z8VCOLDR9FEHH"; // Mã bí mật (Thay của bạn vào)
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    // 2. Hàm tiện ích để mã hóa dữ liệu (Copy y nguyên)
    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                return null;
            }
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
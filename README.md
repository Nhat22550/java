# 🎀 Gundam Store - Hệ Thống Quản Lý Bán Hàng Trực Tuyến

## 📖 Mục Đích Dự Án

**Gundam Store** là một ứng dụng web toàn diện để quản lý bán hàng sản phẩm Figure/Gundam. Hệ thống gồm:
- 🛒 **Gian hàng trực tuyến** cho khách hàng mua sắm
- 🔐 **Hệ thống xác thực** với JWT (JSON Web Token)
- 💳 **Thanh toán VNPay** tích hợp
- 👨‍💼 **Bảng điều khiển Admin** để quản lý sản phẩm, đơn hàng, người dùng
- 📊 **Các truy vấn nâng cao** (Group By, thống kê doanh thu)

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────────────┐
│                    REACT FRONTEND (port 3000)               │
│  - Trang chủ, Đăng nhập, Giỏ hàng, Thanh toán              │
│  - Bảng điều khiển Admin                                    │
└─────────────────────────┬───────────────────────────────────┘
                          │ (Fetch API + JWT Token)
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                  SPRING BOOT BACKEND (port 8080)            │
│  - REST API (Controllers)                                    │
│  - Xác thực + Phân quyền (Spring Security + JWT)           │
│  - Xử lý logic kinh doanh (Services)                        │
│  - Truy cập dữ liệu (Repository)                            │
└─────────────────────────┬───────────────────────────────────┘
                          │ (JDBC)
                          ↓
┌─────────────────────────────────────────────────────────────┐
│              PostgreSQL DATABASE                             │
│  - Users, Roles, Products, Categories, Orders              │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 Cơ Sở Dữ Liệu (Database)

### Bảng chính:

| Bảng | Mô Tả | Khóa chính |
|------|-------|----------|
| **users** | Tài khoản người dùng | id |
| **roles** | Vai trò (ADMIN, USER) | id |
| **users_roles** | Liên kết User-Role (Many-to-Many) | user_id, role_id |
| **categories** | Danh mục sản phẩm | id |
| **products** | Thông tin sản phẩm (tên, giá, số lượng) | id |
| **orders** | Đơn hàng | id |
| **order_details** | Chi tiết từng sản phẩm trong đơn hàng | id |

---

## 🔑 Các Tính Năng Chính

### 1️⃣ Xác Thực & Phân Quyền

**Endpoint:**
- `POST /api/auth/register` - Đăng ký tài khoản mới
- `POST /api/auth/login` - Đăng nhập, nhận JWT Token

**JWT Token:**
```
Header: Authorization: Bearer <token>
```

**Quyền trong hệ thống:**
- `ADMIN` - Quản lý sản phẩm, danh mục, người dùng, đơn hàng
- `USER` - Mua hàng, xem lịch sử đơn hàng

---

### 2️⃣ Quản Lý Sản Phẩm (Products)

**Các trường sản phẩm:**
```java
{
  "id": 1,
  "name": "Gundam RX-78-2",
  "description": "Mobile Suit từ series gốc",
  "price": 299.99,
  "quantity": 50,
  "image": "http://...",
  "series": "Mobile Suit Gundam",          // ← Mới
  "scale": "1/144",                         // ← Mới
  "manufacturer": "Bandai",                 // ← Mới
  "status": "Sẵn hàng",                    // ← Mới
  "categoryId": 5,
  "categoryName": "Real Grade"
}
```

**Endpoint:**
- `GET /api/products` - Lấy danh sách tất cả sản phẩm (Khách xem được)
- `GET /api/products/{id}` - Chi tiết 1 sản phẩm (HATEOAS links)
- `GET /api/products/search?keyword=...&minPrice=...&maxPrice=...` - Tìm kiếm nâng cao
- `GET /api/products/stats` - Thống kê theo danh mục (Group By)
- `GET /api/products/high-revenue` - Sản phẩm doanh thu cao
- `POST /api/products` - Thêm sản phẩm (Admin)
- `PUT /api/products/{id}` - Sửa sản phẩm (Admin)
- `DELETE /api/products/{id}` - Xóa sản phẩm (Admin)

---

### 3️⃣ Quản Lý Danh Mục (Categories)

**Endpoint:**
- `GET /api/categories` - Lấy danh sách danh mục (Khách xem được)
- `GET /api/categories/{id}` - Chi tiết 1 danh mục
- `POST /api/categories` - Thêm danh mục (Admin)
- `PUT /api/categories/{id}` - Sửa danh mục (Admin)
- `DELETE /api/categories/{id}` - Xóa danh mục (Admin)

---

### 4️⃣ Giỏ Hàng & Đặt Hàng (Orders)

**Đặt hàng:**
```javascript
POST /api/orders/place
{
  "userId": 1,
  "address": "123 Nguyễn Huệ, HCMC",
  "phone": "0909999999",
  "paymentMethod": 1,  // 0: COD, 1: VNPay
  "cartItems": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

**Endpoint:**
- `POST /api/orders/place` - Đặt hàng (User đã đăng nhập)
- `GET /api/orders` - Lấy tất cả đơn hàng (Admin)
- `GET /api/orders/confirm-payment/{orderId}` - Cập nhật trạng thái thanh toán

---

### 5️⃣ Thanh Toán VNPay

**Flow thanh toán:**
```
1. User tạo đơn hàng (Order tạo ra, trạng thái: "Chờ thanh toán")
   
2. Backend tạo link thanh toán VNPay:
   POST /api/payment/create_payment?amount=299990&orderId=5&orderInfo=Gundam
   
3. Trả về response:
   {
     "status": "OK",
     "URL": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?..."
   }
   
4. Frontend redirect khách sang VNPay để thanh toán
   
5. VNPay xác nhận ✓ → Redirect về:
   http://localhost:3000/payment-result
   
6. React gọi:
   GET /api/orders/confirm-payment/{orderId}
   
7. Đơn hàng cập nhật: "Đã thanh toán (VNPay)" ✓
```

**Cấu hình VNPay** (file [VNPayConfig.java](src/main/java/com/example/demo/config/VNPayConfig.java#L1)):
```java
public static String vnp_TmnCode = "CQ0JGRS1";        // ⚠️ Thay bằng mã của bạn
public static String vnp_HashSecret = "8LOT3...";    // ⚠️ Thay bằng khóa của bạn
```

👉 **Đăng ký VNPay Sandbox miễn phí tại:** https://sandbox.vnpayment.vn/devreg/

---

### 6️⃣ Quản Lý Người Dùng (Users)

**Endpoint:**
- `GET /api/users` - Xem danh sách User (Admin)
- `DELETE /api/users/{id}` - Xóa User (Admin)

---

### 7️⃣ Quản Lý Đơn Hàng (Orders Management)

**Endpoint:**
- `GET /api/orders` - Xem tất cả đơn hàng (Admin)

---

## ⚙️ Cấu Hình Backend

### 📝 File: [application.properties](src/main/resources/application.properties)

```properties
# Cơ sở dữ liệu
spring.datasource.url=jdbc:postgresql://localhost:5432/demoproduct
spring.datasource.username=postgres
spring.datasource.password=Giangminhnhat22!
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**⚠️ Lưu ý:** Thay đổi `password` thành mật khẩu PostgreSQL của bạn!

---

## 📦 Công Nghệ Sử Dụng

### Backend:
| Công Nghệ | Phiên Bản | Mục Đích |
|-----------|----------|---------|
| **Java** | 21 | Ngôn ngữ lập trình |
| **Spring Boot** | 4.0.1 | Framework web |
| **Spring Security** | - | Xác thực & phân quyền |
| **Spring Data JPA** | - | ORM, truy cập DB |
| **PostgreSQL** | - | Cơ sở dữ liệu |
| **JWT (JJWT)** | 0.11.5 | Token xác thực |
| **Lombok** | - | Giảm boilerplate code |
| **Maven** | - | Build tool |

### Frontend:
| Công Nghệ | Mục Đích |
|-----------|---------|
| **React** | Framework UI |
| **React Router** | Định tuyến URL |
| **Bootstrap 5** | CSS Framework |
| **Axios** | HTTP client |

---

## 🚀 Hướng Dẫn Chạy Dự Án

### **Bước 1: Chuẩn Bị Môi Trường**

```bash
# 1. Cài đặt PostgreSQL (nếu chưa có)
# https://www.postgresql.org/download/

# 2. Tạo database
createdb demoproduct

# 3. Cài Java 21 JDK
# https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html

# 4. Cài Node.js (React)
# https://nodejs.org/
```

---

### **Bước 2: Chạy Backend (Spring Boot)**

```bash
# 1. Vào thư mục gốc
cd c:\Users\Admin\Downloads\nhat doanjava

# 2. Build Maven
mvn clean package

# 3. Chạy ứng dụng
# Cách 1: Command line
mvn spring-boot:run

# Cách 2: IDE (VS Code)
# Cài extension "Extension Pack for Java"
# Ấn F5 hoặc Ctrl+F5 để debug

# ✅ Backend sẽ chạy ở: http://localhost:8080
```

---

### **Bước 3: Chạy Frontend (React)**

```bash
# 1. Vào thư mục client
cd c:\Users\Admin\Downloads\nhat doanjava\client

# 2. Cài dependencies
npm install

# 3. Chạy React
npm start

# ✅ Frontend sẽ mở ở: http://localhost:3000
```

---

## 📡 API Endpoints Summary

### 🔓 Public (Không cần đăng nhập):
```
POST   /api/auth/register
POST   /api/auth/login
GET    /api/products
GET    /api/products/{id}
GET    /api/products/search
GET    /api/products/stats
GET    /api/products/high-revenue
GET    /api/categories
GET    /api/categories/{id}
GET    /api/payment/create_payment
GET    /api/orders/confirm-payment/{orderId}
```

### 🔐 User (Cần đăng nhập):
```
POST   /api/orders/place        (Người dùng mua hàng)
```

### 👑 Admin (Cần token + quyền ADMIN):
```
POST   /api/products            (Thêm sản phẩm)
PUT    /api/products/{id}       (Sửa sản phẩm)
DELETE /api/products/{id}       (Xóa sản phẩm)

POST   /api/categories          (Thêm danh mục)
PUT    /api/categories/{id}     (Sửa danh mục)
DELETE /api/categories/{id}     (Xóa danh mục)

GET    /api/users               (Xem danh sách user)
DELETE /api/users/{id}          (Xóa user)

GET    /api/orders              (Xem tất cả đơn hàng)
```

---

## 🔐 Cách Dùng JWT Token

### **1. Đăng Nhập & Lấy Token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "username": "admin",
  "roles": [{"id": 1, "name": "ADMIN"}],
  "user": {...}
}
```

### **2. Dùng Token trong Request:**
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## 📁 Cấu Trúc Thư Mục

### Backend:
```
src/main/java/com/example/demo/
├── DemoApplication.java          (Entry point)
├── config/
│   ├── SecurityConfig.java       (Cấu hình Spring Security)
│   ├── VNPayConfig.java          (Cấu hình VNPay)
│   └── WebConfig.java            (Cấu hình CORS)
├── controller/                   (REST API endpoints)
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CategoryController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   └── UserController.java
├── dto/                          (Data Transfer Objects)
│   ├── ProductDto.java
│   ├── AuthRequest.java
│   ├── OrderRequest.java
│   ├── CartItemDto.java
│   └── PaymentResDTO.java
├── entity/                       (Database entities)
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Role.java
│   ├── Product.java
│   ├── Category.java
│   ├── Order.java
│   └── OrderDetail.java
├── service/                      (Business logic)
│   ├── ProductService.java
│   ├── ProductServiceImpl.java
│   ├── CategoryService.java
│   ├── OrderService.java
│   └── UserDetailsServiceImpl.java
├── repository/                   (Database access)
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CategoryRepository.java
│   ├── OrderRepository.java
│   ├── OrderDetailRepository.java
│   └── RoleRepository.java
├── filter/
│   └── JwtAuthenticationFilter.java   (JWT validation)
├── mapper/
│   └── ProductMapper.java             (Entity ↔ DTO)
├── util/
│   └── JwtTokenProvider.java          (Create & validate JWT)
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

### Frontend:
```
client/src/
├── index.js
├── App.js                        (Router chính)
├── components/
│   ├── Home.js
│   ├── Login.js
│   ├── Register.js
│   ├── admin/
│   │   ├── Dashboard.js
│   │   ├── AdminSidebar.js
│   │   ├── ProductList.js
│   │   ├── ProductForm.js
│   │   ├── CategoryManager.js
│   │   ├── OrderManager.js
│   │   └── UserManager.js
│   ├── user/
│   │   ├── Cart.js
│   │   └── PaymentResult.js
│   └── layout/
│       ├── Banner.js
│       └── Footer.js
└── services/
    ├── ProductService.js
    ├── CategoryService.js
    ├── OrderService.js
    └── UserService.js
```

---

## 🧪 Ví Dụ Thực Hành

### Thêm Sản Phẩm Gundam Mới:

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MG Gundam Wing Zero",
    "description": "Mobile Suit từ Gundam Wing",
    "price": 4500000,
    "quantity": 20,
    "image": "https://...",
    "series": "Gundam Wing",
    "scale": "1/100",
    "manufacturer": "Bandai",
    "status": "Sẵn hàng",
    "categoryId": 3
  }'
```

### Tìm Kiếm Sản Phẩm Gundam:

```bash
curl http://localhost:8080/api/products/search?keyword=Gundam&minPrice=1000000&maxPrice=5000000
```

### Thống Kê Sản Phẩm Theo Hãng:

```bash
curl http://localhost:8080/api/products/stats
```

---

## 🐛 Xử Lý Lỗi Thường Gặp

| Lỗi | Nguyên Nhân | Cách Giải Quyết |
|-----|-----------|-----------------|
| **Connection refused (port 8080)** | Backend chưa chạy | `mvn spring-boot:run` |
| **CORS error** | Frontend & backend khác origin | Kiểm tra [WebConfig.java](src/main/java/com/example/demo/config/WebConfig.java) |
| **JWT expired** | Token hết hạn | Đăng nhập lại để lấy token mới |
| **403 Forbidden** | Không có quyền Admin | Kiểm tra role của user trong database |
| **Database connection failed** | PostgreSQL chưa chạy | `psql -U postgres` để test kết nối |
| **Sản phẩm không đủ kho** | Số lượng mua > số lượng tồn | Cập nhật số lượng sản phẩm |

---

## 📚 Tài Liệu Tham Khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Tutorial](https://jwt.io/)
- [VNPay Integration Guide](https://sandbox.vnpayment.vn/docs/)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

## 👨‍💻 Tác Giả

Dự án được phát triển bởi **Minh Nhật - Gundam Enthusiast** 🎀

---

## 📄 Giấy Phép

Dự án này được phân phối dưới giấy phép MIT. Xem file [LICENSE](LICENSE) để biết chi tiết.

---

## 💬 Liên Hệ & Hỗ Trợ

Nếu có bất kỳ câu hỏi hoặc cần hỗ trợ:
- 📧 Email: minhhat@example.com
- 💻 GitHub: [nhat-doan-java](https://github.com)
- 📱 Facebook: [Gundam Store Official](https://facebook.com)

---

**Happy Coding! 🚀 Chúc bạn mua sắm vui vẻ trên Gundam Store! 🎀**

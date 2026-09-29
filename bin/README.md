# BT JWT NIMBUS - SQL Server

**Sinh viên:** Trần Quyết Chiến  
**MSSV:** 24110171  
**Email demo:** chien@gmail.com

Project dùng **Spring Boot 3 + Spring Security 6 + Nimbus JOSE + JWT + SQL Server** và đã chỉnh để bám đầy đủ luồng ví dụ JWT trong bài giảng, nhưng thay thư viện JJWT bằng **Nimbus**.

## Các phần đã làm theo ví dụ

1. Dependency JWT: dùng `com.nimbusds:nimbus-jose-jwt` thay cho `io.jsonwebtoken`.
2. Entity `User` implements `UserDetails`, có `id`, `fullName`, `email`, `images`, `password`, thời gian tạo/cập nhật.
3. Models/DTO: đăng ký, đăng nhập, login response, user response.
4. `UserRepository`, `UserService`, `AuthenticationService`, `JwtService`.
5. `ApplicationConfiguration`: `UserDetailsService`, BCrypt, `AuthenticationManager`, `AuthenticationProvider`.
6. `AuthenticationFilter`: đọc `Authorization: Bearer <token>` và xác thực JWT.
7. `SecurityConfiguration`: stateless, filter, CORS, URL public/protected.
8. REST Controller cho `/auth/signup`, `/auth/login`, `/users/me`, `/users`.
9. Test luồng: đăng ký -> đăng nhập -> sinh JWT -> gọi `/users/me` và `/users` bằng Bearer token.
10. Exception: sai email/mật khẩu `401`, tài khoản khóa `403`, access denied `403`, JWT sai chữ ký `401`, JWT hết hạn `401`.
11. Render Ajax: `login.html`, `profile.html`, `AuthController.java`, `mainjs.js` dùng `localStorage` và AJAX/fetch.

## Chạy nhanh - chỉ import rồi Run

Yêu cầu máy đã có:

- JDK 17 trở lên.
- SQL Server đang chạy.
- SQL Server Authentication cho tài khoản `sa` hoạt động.
- TCP/IP SQL Server dùng port `1433`.

Thực hiện:

1. Giải nén project.
2. Mở IntelliJ IDEA / Eclipse / Spring Tool Suite.
3. Open/Import Maven Project tại thư mục có `pom.xml`.
4. Đợi Maven tải dependency.
5. Run:

```text
src/main/java/com/hcmute/jwtnimbus/JwtNimbusApplication.java
```

6. Mở:

```text
http://localhost:8080
```

Project tự kết nối SQL Server, tự tạo database `jwt_nimbus_24110171` nếu chưa có, Hibernate tạo/cập nhật bảng `app_users`, rồi tạo sẵn tài khoản demo.

Khi khởi động, project cũng chuẩn hóa cột họ tên sang `NVARCHAR`, sửa lại họ tên tiếng Việt của tài khoản `chien@gmail.com` và xóa tài khoản cũ `admin@gmail.com` (Admin Demo) nếu còn trong database.

## Tài khoản demo

```text
Họ tên: Trần Quyết Chiến
Email: chien@gmail.com
Password: 123456
Role: ADMIN
Images: tran-quyet-chien.jpg
```

## Cấu hình SQL Server local

File local:

```text
src/main/resources/application-local.properties
```

File này đã có cấu hình chạy trên máy của sinh viên và nằm trong `.gitignore`, nên `git add .` sẽ không đưa thông tin đăng nhập SQL Server lên GitHub.

## Nimbus nằm ở đâu?

Dependency:

```xml
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>9.37.3</version>
</dependency>
```

Code chính:

```text
src/main/java/com/hcmute/jwtnimbus/service/JwtService.java
```

Các class Nimbus sử dụng:

- `SignedJWT`
- `JWTClaimsSet`
- `JWSHeader`
- `MACSigner`
- `MACVerifier`
- `JWSAlgorithm.HS256`

Project không dùng `io.jsonwebtoken` / JJWT.

## Luồng hoạt động

```text
Đăng nhập
  -> AuthenticationManager kiểm tra email/password
  -> JwtService dùng Nimbus sinh JWT
  -> mainjs.js lưu token vào localStorage
  -> request gửi Authorization: Bearer <token>
  -> AuthenticationFilter đọc token
  -> Nimbus verify chữ ký + expiration
  -> SecurityContext được thiết lập
  -> truy cập /users/me hoặc /users
```

## API test

### Đăng ký

```http
POST /auth/signup
Content-Type: application/json

{
  "fullName": "Trần Quyết Chiến",
  "email": "chien@gmail.com",
  "password": "123456"
}
```

### Đăng nhập

```http
POST /auth/login
Content-Type: application/json

{
  "email": "chien@gmail.com",
  "password": "123456"
}
```

### API cần JWT

```http
GET /users/me
Authorization: Bearer <token>
```

```http
GET /users
Authorization: Bearer <token>
```

## File quan trọng để thầy hỏi

```text
entity/User.java
configuration/ApplicationConfiguration.java
configuration/SecurityConfiguration.java
filter/AuthenticationFilter.java
service/JwtService.java
service/AuthenticationService.java
controller/AuthenticationController.java
controller/UserController.java
controller/AuthController.java
static/js/mainjs.js
```

## Up GitHub

```bash
git init
git add .
git commit -m "Hoan thanh bai tap JWT Nimbus"
git branch -M main
git remote add origin LINK_REPOSITORY_CUA_BAN
git push -u origin main
```

Không dùng `git add -f` cho `application-local.properties` vì file này chứa cấu hình SQL Server local.

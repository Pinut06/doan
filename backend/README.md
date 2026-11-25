# TechGear — Website bán phụ kiện công nghệ

Spring Boot 3 · Thymeleaf · Spring Data JPA · Spring Security · MySQL

## Yêu cầu
- JDK 17+
- Maven 3.9+ (hoặc dùng IDE: IntelliJ / VS Code / Eclipse)
- MySQL đang chạy ở `localhost:3306`

## Chạy dự án
1. Mở `src/main/resources/application.properties`, sửa `spring.datasource.username` / `password` cho đúng MySQL của bạn
   (mặc định: `root`, mật khẩu rỗng — giống project trà sữa cũ).
2. Database `doan` **tự được tạo**, các bảng cũng tự tạo (`ddl-auto=update`).
3. Chạy:
   ```
   mvn spring-boot:run
   ```
4. Mở http://localhost:8082

Lần chạy đầu tiên, `DataInitializer` sẽ tạo sẵn tài khoản và 6 danh mục + 13 sản phẩm mẫu.

## Tài khoản mặc định
| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Admin | `admin` | `admin123` |
| Khách | `khach` | `khach123` |

> Hãy đổi mật khẩu admin (hoặc xóa tài khoản mẫu) trước khi đưa lên môi trường thật.

## Chức năng
**Khách hàng**
- Trang chủ: sản phẩm nổi bật, lọc theo danh mục, tìm kiếm theo tên/thương hiệu
- Chi tiết sản phẩm: giá, thương hiệu, bảo hành, tình trạng kho, sản phẩm liên quan
- Đăng ký / đăng nhập / đăng xuất

**Admin** (`/admin`, chỉ role ADMIN)
- Tổng quan: thống kê, cảnh báo sản phẩm sắp hết hàng
- Sản phẩm: thêm / sửa / xóa, upload ảnh, ẩn/hiện, đánh dấu nổi bật / bán chạy
- Danh mục: thêm / sửa / xóa (không cho xóa nếu còn sản phẩm)
- Tài khoản: xem danh sách, khóa / mở khóa, xóa

## Cấu trúc
```
src/main/java/com/example/phukien
├── config/        SecurityConfig, WebConfig, DataInitializer
├── model/         DanhMuc, SanPham, TaiKhoan   (Entity)
├── repository/    JpaRepository cho từng Entity
├── service/       Nghiệp vụ + FileStorageService (lưu ảnh)
└── controller/    HomeController, AuthController, admin/*
src/main/resources
├── templates/     Thymeleaf (home, product-detail, login, register, admin/*)
└── static/css/    style.css
```
Luồng giống tài liệu LyThuyet.java của bạn: Controller → Service → Repository → Entity → Database.

## Ghi chú
- Ảnh upload được lưu ở thư mục `uploads/` cạnh nơi chạy ứng dụng và phục vụ qua `/images/...`.
- Mật khẩu được mã hóa BCrypt.
- Form POST đều có CSRF token (Thymeleaf tự thêm).

## Mua hàng (bổ sung)
- **Giỏ hàng** (`/gio-hang`): lưu trong MySQL (bảng `gio_hang`, mỗi khách một giỏ nên không mất khi đăng xuất / tắt máy); thêm / cập nhật / xóa, số lượng hiện trên header. Cần đăng nhập bằng tài khoản khách hàng.
- **Mua ngay / Thêm vào giỏ**: nút ở trang chi tiết sản phẩm.
- **Thanh toán** (`/thanh-toan`, cần đăng nhập): nhập thông tin nhận hàng, chọn phương thức thanh toán, tạo đơn.
- **Đơn hàng của tôi** (`/don-hang`): xem chi tiết, hủy đơn, thanh toán online.
- **Preorder**: admin tick "Cho phép đặt trước" + ngày dự kiến ở form sản phẩm; khi hết hàng khách vẫn đặt được, đơn đặt trước tách riêng (mã `PT...`, đơn thường `PG...`).
- **Bình luận & đánh giá sao** ở trang sản phẩm (cần đăng nhập; chủ bình luận hoặc admin được xóa).
- **Mô tả**: khối "Mô tả sản phẩm" riêng — dòng dạng `Tên: giá trị` thành bảng thông số, dòng còn lại thành "Điểm nổi bật".
- **Admin**: quản lý đơn hàng tại `/admin/don-hang`.
- Các cột/bảng mới (`dat_truoc`, `ngay_du_kien`, `don_hang`, `chi_tiet_don_hang`, `binh_luan`, `gio_hang`) tự tạo nhờ `ddl-auto=update`.

## Tách admin và khách hàng
- Quản trị viên nằm ở bảng **`quan_tri`**, người mua nằm ở bảng **`khach_hang`** (không còn bảng `tai_khoan` và cột `vai_tro`).
- Đăng nhập dùng chung một trang `/login`: hệ thống tìm ở `quan_tri` trước (quyền ADMIN), không có thì tìm ở `khach_hang` (quyền USER). Tên đăng nhập là duy nhất trên cả hai bảng.
- Admin chỉ vào `/admin`; admin không mua hàng / bình luận (nhưng được xóa bình luận). Giỏ hàng, đơn hàng, bình luận gắn với `khach_hang`.
- Trang `/admin/tai-khoan` giờ quản lý **khách hàng** (khóa / mở khóa / xóa).
- **Đang có dữ liệu cũ?** Chạy file `nang_cap_tach_tai_khoan.sql` một lần trong phpMyAdmin (tab SQL) trước khi chạy bản mới. Chưa có dữ liệu cần giữ thì bỏ qua: lần chạy đầu ứng dụng tự tạo `admin / admin123` và `khach / khach123`.

package com.example.phukien.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HoSoKhachHang(
        @Size(max = 255, message = "Họ tên tối đa 255 ký tự.") String hoTen,
        @Email(message = "Email không hợp lệ.")
        @Size(max = 255, message = "Email tối đa 255 ký tự.") String email,
        @Pattern(regexp = "(?:0|\\+84)[0-9]{9,10}", message = "Số điện thoại phải bắt đầu bằng 0 hoặc +84, theo sau là 9 đến 10 chữ số.")
        String soDienThoai,
        @Size(max = 500, message = "Địa chỉ tối đa 500 ký tự.") String diaChi,
        @NotNull(message = "Vui lòng chọn nhóm khách hàng.") NhomKhachHang nhomKhachHang) {

    public HoSoKhachHang {
        hoTen = chuanHoa(hoTen);
        email = chuanHoa(email);
        soDienThoai = chuanHoa(soDienThoai);
        diaChi = chuanHoa(diaChi);
    }

    public static HoSoKhachHang tu(KhachHang kh) {
        return new HoSoKhachHang(kh.getHoTen(), kh.getEmail(), kh.getSoDienThoai(), kh.getDiaChi(), kh.getNhomKhachHang());
    }

    private static String chuanHoa(String giaTri) {
        return giaTri == null || giaTri.isBlank() ? null : giaTri.strip();
    }
}

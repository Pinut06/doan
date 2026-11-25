package com.example.phukien.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@DynamicUpdate
@Table(name = "khach_hang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "khach_hang_id")
    private Integer idKhachHang;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "email")
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "dia_chi", length = 500)
    private String diaChi;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnDefault("'THUONG'")
    @Column(name = "nhom_khach_hang", nullable = false, length = 20)
    private NhomKhachHang nhomKhachHang = NhomKhachHang.THUONG;

    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    public KhachHang() {
    }

    public Integer getIdKhachHang() { return idKhachHang; }
    public void setIdKhachHang(Integer idKhachHang) { this.idKhachHang = idKhachHang; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getKichHoat() { return kichHoat; }
    public void setKichHoat(Boolean kichHoat) { this.kichHoat = kichHoat; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public NhomKhachHang getNhomKhachHang() { return nhomKhachHang; }
    public void setNhomKhachHang(NhomKhachHang nhomKhachHang) { this.nhomKhachHang = nhomKhachHang; }
}

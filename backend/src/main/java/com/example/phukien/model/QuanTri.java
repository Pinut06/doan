package com.example.phukien.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quan_tri")
public class QuanTri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quan_tri_id")
    private Integer idQuanTri;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "email")
    private String email;

    @Column(name = "kich_hoat")
    private Boolean kichHoat = true;

    public QuanTri() {
    }

    public Integer getIdQuanTri() { return idQuanTri; }
    public void setIdQuanTri(Integer idQuanTri) { this.idQuanTri = idQuanTri; }
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
}

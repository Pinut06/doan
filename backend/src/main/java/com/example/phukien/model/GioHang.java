package com.example.phukien.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gio_hang",
        uniqueConstraints = @UniqueConstraint(columnNames = { "khach_hang_id", "san_pham_id" }))
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gio_hang_id")
    private Integer idGioHang;

    @ManyToOne(optional = false)
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;

    @ManyToOne(optional = false)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    public GioHang() {
    }

    public Integer getIdGioHang() { return idGioHang; }
    public void setIdGioHang(Integer idGioHang) { this.idGioHang = idGioHang; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}

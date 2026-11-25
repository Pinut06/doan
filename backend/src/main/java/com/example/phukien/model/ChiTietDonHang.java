package com.example.phukien.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chi_tiet_id")
    private Integer idChiTiet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @ManyToOne(optional = false)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;

    @Column(name = "ten_san_pham", nullable = false)
    private String tenSanPham;

    @Column(name = "don_gia", precision = 15, scale = 0)
    private BigDecimal donGia;

    @Column(name = "gia_von_khi_ban", precision = 15, scale = 0)
    private BigDecimal giaVonKhiBan;

    public void chotGiaVon() { giaVonKhiBan = sanPham.getGiaVon(); }

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    public ChiTietDonHang() {
    }

    public BigDecimal getThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    public Integer getIdChiTiet() { return idChiTiet; }
    public void setIdChiTiet(Integer idChiTiet) { this.idChiTiet = idChiTiet; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}

package com.example.phukien.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class DongHoaDon {
    @Column(name = "ten_hang", nullable = false)
    private String ten;
    @Column(name = "so_luong", nullable = false)
    private int soLuong;
    @Column(name = "don_gia", nullable = false, precision = 15, scale = 0)
    private BigDecimal donGia;

    protected DongHoaDon() {}
    public DongHoaDon(String ten, int soLuong, BigDecimal donGia) {
        this.ten = ten;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }
    public String getTen() { return ten; }
    public int getSoLuong() { return soLuong; }
    public BigDecimal getDonGia() { return donGia; }
    public BigDecimal getThanhTien() { return donGia.multiply(BigDecimal.valueOf(soLuong)); }

    @Override public boolean equals(Object other) {
        return other instanceof DongHoaDon d && soLuong == d.soLuong && Objects.equals(ten, d.ten)
                && Objects.equals(donGia, d.donGia);
    }
    @Override public int hashCode() { return Objects.hash(ten, soLuong, donGia); }
}

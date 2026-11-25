package com.example.phukien.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import jakarta.persistence.*;

@Embeddable
public class UuDaiDon {
    public enum Loai { PHAN_TRAM, SO_TIEN }

    @Column(name = "ma_giam_gia", length = 32)
    private String ma;
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", length = 16)
    private Loai loai;
    @Column(name = "muc_giam", precision = 15, scale = 0)
    private BigDecimal muc;
    @Column(name = "nguong_ap_dung", precision = 15, scale = 0)
    private BigDecimal nguong;
    @Column(name = "chiet_khau", precision = 15, scale = 0)
    private BigDecimal tienGiam;

    protected UuDaiDon() {}

    public UuDaiDon(String ma, Loai loai, BigDecimal muc, BigDecimal nguong, BigDecimal tienHang) {
        this.ma = ma;
        this.loai = loai;
        this.muc = muc;
        this.nguong = nguong;
        tinhLai(tienHang);
    }

    public BigDecimal tinhLai(BigDecimal tienHang) {
        tienGiam = BigDecimal.ZERO;
        if (tienHang.compareTo(nguong) >= 0) {
            tienGiam = loai == Loai.PHAN_TRAM
                    ? tienHang.multiply(muc).movePointLeft(2).setScale(0, RoundingMode.HALF_UP)
                    : muc.min(tienHang);
        }
        return tienHang.subtract(tienGiam);
    }

    public String getMa() { return ma; }
    public Loai getLoai() { return loai; }
    public BigDecimal getMuc() { return muc; }
    public BigDecimal getNguong() { return nguong; }
    public BigDecimal getTienGiam() { return tienGiam; }
}

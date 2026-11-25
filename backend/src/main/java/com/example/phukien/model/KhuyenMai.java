package com.example.phukien.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@org.hibernate.annotations.Check(constraints = "phan_tram between 1 and 100 and tu_ngay <= den_ngay")
@Table(name = "khuyen_mai", indexes = @Index(name = "idx_km_sp_ngay", columnList = "san_pham_id,tu_ngay,den_ngay"))
public class KhuyenMai {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "khuyen_mai_id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;
    @Column(nullable = false, length = 120)
    private String ten;
    @Column(name = "phan_tram", nullable = false)
    private int phanTram;
    @Column(name = "tu_ngay", nullable = false)
    private LocalDate tuNgay;
    @Column(name = "den_ngay", nullable = false)
    private LocalDate denNgay;

    protected KhuyenMai() {}

    public KhuyenMai(SanPham sanPham) { this.sanPham = sanPham; }

    public void capNhat(String ten, int phanTram, LocalDate tuNgay, LocalDate denNgay) {
        this.ten = ten;
        this.phanTram = phanTram;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
    }

    public boolean apDung(LocalDate ngay) { return !ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay); }
    public Integer getId() { return id; }
    public String getTen() { return ten; }
    public int getPhanTram() { return phanTram; }
    public LocalDate getTuNgay() { return tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
}

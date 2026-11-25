package com.example.phukien.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "nhat_ky_giao", indexes = @Index(name = "idx_nk_giao", columnList = "giao_hang_id,id"))
public class NhatKyGiao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "giao_hang_id", nullable = false)
    private GiaoHang giaoHang;
    @Column(nullable = false)
    private LocalDateTime thoiDiem;
    @Column(nullable = false, length = 255)
    private String nguoiGhi;
    @Column(nullable = false, length = 100)
    private String suKien;
    @Column(nullable = false, length = 500)
    private String noiDung;

    protected NhatKyGiao() {}
    public NhatKyGiao(GiaoHang giaoHang, LocalDateTime thoiDiem, String nguoiGhi, String suKien, String noiDung) {
        this.giaoHang = giaoHang;
        this.thoiDiem = thoiDiem;
        this.nguoiGhi = nguoiGhi;
        this.suKien = suKien;
        this.noiDung = noiDung;
    }
    public LocalDateTime getThoiDiem() { return thoiDiem; }
    public String getNguoiGhi() { return nguoiGhi; }
    public String getSuKien() { return suKien; }
    public String getNoiDung() { return noiDung; }
}

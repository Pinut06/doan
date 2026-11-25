package com.example.phukien.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "giao_hang", uniqueConstraints = @UniqueConstraint(name = "uq_giao_van_don", columnNames = {"don_vi", "ma_van_don"}))
public class GiaoHang {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "don_hang_id", nullable = false, unique = true)
    private DonHang don;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BuocGiao buoc;
    @Column(nullable = false)
    private long lanCapNhat;
    @Column(nullable = false)
    private boolean duLieuCu;
    @Column(length = 100)
    private String donVi;
    @Column(length = 80)
    private String maVanDon;
    private LocalDateTime ngayYeuCau;
    private LocalDateTime ngayLapPhieu;
    @Column(length = 255)
    private String nguoiLapPhieu;
    @Column(precision = 15, scale = 0)
    private BigDecimal thuHoKhiLap;

    protected GiaoHang() {}
    public GiaoHang(DonHang don, BuocGiao buoc, boolean duLieuCu) {
        this.don = don;
        this.buoc = buoc;
        this.duLieuCu = duLieuCu;
    }
    public void yeuCau(LocalDateTime ngay) { ngayYeuCau = ngay; }
    public void lapPhieu(String donVi, String maVanDon, LocalDateTime ngay, String nguoiLap) {
        this.donVi = donVi;
        this.maVanDon = maVanDon;
        this.ngayLapPhieu = ngay;
        this.nguoiLapPhieu = nguoiLap;
        this.thuHoKhiLap = don.getPhuongThuc() == PhuongThucThanhToan.COD ? don.getConNo() : BigDecimal.ZERO;
    }
    public void chuyen(BuocGiao buoc) { this.buoc = buoc; lanCapNhat++; }
    public Integer getId() { return id; }
    public BuocGiao getBuoc() { return buoc; }
    public long getLanCapNhat() { return lanCapNhat; }
    public boolean isDuLieuCu() { return duLieuCu; }
    public String getSoYeuCau() { return ngayYeuCau == null ? null : String.format("YX-%06d", id); }
    public String getSoPhieu() { return ngayLapPhieu == null ? null : String.format("PGH-%06d", id); }
    public String getDonVi() { return donVi; }
    public String getMaVanDon() { return maVanDon; }
    public String getNguoiLapPhieu() { return nguoiLapPhieu; }
    public BigDecimal getThuHoKhiLap() { return thuHoKhiLap; }
    public LocalDateTime getNgayYeuCau() { return ngayYeuCau; }
    public LocalDateTime getNgayLapPhieu() { return ngayLapPhieu; }
}

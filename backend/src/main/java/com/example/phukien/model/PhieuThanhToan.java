package com.example.phukien.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "phieu_thanh_toan", indexes = @Index(name = "idx_phieu_don", columnList = "don_hang_id,id"))
public class PhieuThanhToan {
    public enum Loai { THU, HOAN }
    public enum HinhThuc {
        TIEN_MAT("Tiền mặt"), CHUYEN_KHOAN("Chuyển khoản"), SO_DU_DAU("Số dư đầu kỳ");
        private final String nhan;
        HinhThuc(String nhan) { this.nhan = nhan; }
        public String getNhan() { return nhan; }
    }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "don_hang_id", nullable = false)
    private DonHang don;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12)
    private Loai loai;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private HinhThuc hinhThuc;
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal soTien;
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal giamSauBan;
    @Column(nullable = false, length = 120, unique = true)
    private String yeuCau;
    @Column(length = 110, unique = true)
    private String doiSoat;
    @Column(nullable = false, length = 255)
    private String nguoiLap;
    @Column(nullable = false, length = 255)
    private String nguoiNopNhan;
    @Column(nullable = false, length = 255)
    private String noiDung;
    @Column(nullable = false)
    private LocalDateTime ngayLap;

    protected PhieuThanhToan() {}
    public PhieuThanhToan(DonHang don, Loai loai, HinhThuc hinhThuc, BigDecimal tien, BigDecimal giam,
                          String yeuCau, String doiSoat, String nguoiLap, String nguoiNopNhan, String noiDung) {
        this.don = don;
        this.loai = loai;
        this.hinhThuc = hinhThuc;
        this.soTien = tien;
        this.giamSauBan = giam;
        this.yeuCau = yeuCau;
        this.doiSoat = doiSoat;
        this.nguoiLap = nguoiLap;
        this.nguoiNopNhan = nguoiNopNhan;
        this.noiDung = noiDung;
        ngayLap = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }
    public Long getId() { return id; }
    public String getSoPhieu() { return (loai == Loai.THU ? "PT-" : "PH-") + String.format("%06d", id); }
    public DonHang getDon() { return don; }
    public Loai getLoai() { return loai; }
    public HinhThuc getHinhThuc() { return hinhThuc; }
    public BigDecimal getSoTien() { return soTien; }
    public BigDecimal getGiamSauBan() { return giamSauBan; }
    public String getYeuCau() { return yeuCau; }
    public String getDoiSoat() { return doiSoat; }
    public String getNguoiLap() { return nguoiLap; }
    public String getNguoiNopNhan() { return nguoiNopNhan; }
    public String getNoiDung() { return noiDung; }
    public LocalDateTime getNgayLap() { return ngayLap; }
}

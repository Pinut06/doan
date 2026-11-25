package com.example.phukien.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import jakarta.persistence.*;

@Entity
@Table(name = "ma_giam_gia")
public class MaGiamGia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Version
    private long phienBan;
    @Column(nullable = false, unique = true, length = 32)
    private String ma;
    @Column(nullable = false, length = 120)
    private String ten;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private UuDaiDon.Loai loai;
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal muc;
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal nguong;
    @Column(nullable = false)
    private LocalDate tuNgay;
    @Column(nullable = false)
    private LocalDate denNgay;
    @Column(nullable = false)
    private boolean kichHoat;

    protected MaGiamGia() {}

    public MaGiamGia(String ma) { this.ma = chuanHoa(ma); }

    public static String chuanHoa(String ma) {
        return ma == null ? "" : ma.strip().toUpperCase(Locale.ROOT);
    }

    public void capNhat(String ten, UuDaiDon.Loai loai, BigDecimal muc, BigDecimal nguong,
                        LocalDate tuNgay, LocalDate denNgay, boolean kichHoat) {
        if (denNgay.isBefore(tuNgay)) throw new IllegalArgumentException("Ngày kết thúc phải từ ngày bắt đầu trở đi.");
        if (loai == UuDaiDon.Loai.PHAN_TRAM && muc.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Mức giảm theo phần trăm tối đa là 100%.");
        this.ten = ten.strip();
        this.loai = loai;
        this.muc = muc;
        this.nguong = nguong;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.kichHoat = kichHoat;
    }

    public boolean coHieuLuc(LocalDate ngay) {
        return kichHoat && !ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay);
    }

    public Integer getId() { return id; }
    public long getPhienBan() { return phienBan; }
    public String getMa() { return ma; }
    public String getTen() { return ten; }
    public UuDaiDon.Loai getLoai() { return loai; }
    public BigDecimal getMuc() { return muc; }
    public BigDecimal getNguong() { return nguong; }
    public LocalDate getTuNgay() { return tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public boolean isKichHoat() { return kichHoat; }
}

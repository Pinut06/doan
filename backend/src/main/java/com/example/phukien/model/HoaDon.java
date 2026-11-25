package com.example.phukien.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "hoa_don", uniqueConstraints = @UniqueConstraint(columnNames = {"don_hang_id","lan_lap"}))
public class HoaDon {
    public enum Loai {
        BAN_HANG("Bán hàng"), DIEU_CHINH("Điều chỉnh"), THAY_THE("Thay thế");
        private final String nhan;
        Loai(String nhan) { this.nhan = nhan; }
        public String getNhan() { return nhan; }
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "don_hang_id", nullable = false)
    private int donId;
    @Column(nullable = false, length = 30)
    private String maDon;
    @Column(name = "lan_lap", nullable = false)
    private int lanLap;
    @Column(nullable = false)
    private long lanSua;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private Loai loai;
    private Long banTruocId;
    @Column(length = 40)
    private String soThamChieu;
    @Column(unique = true, length = 40)
    private String soHoaDon;
    @Column(length = 40)
    private String thayBoi;
    @Column(nullable = false)
    private String nguoiMua;
    @Column(nullable = false, length = 500)
    private String diaChi;
    @Column(length = 30)
    private String mst;
    @Column(length = 500)
    private String lyDo;
    @Column(nullable = false)
    private String nguoiLap;
    @Column(nullable = false)
    private LocalDateTime ngayLap;
    private String nguoiPhatHanh;
    private LocalDateTime ngayPhatHanh;
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal giam;
    @ElementCollection
    @CollectionTable(name = "dong_hoa_don", joinColumns = @JoinColumn(name = "hoa_don_id"))
    @OrderColumn(name = "vi_tri")
    private List<DongHoaDon> dongs = new ArrayList<>();
    @Lob @Column(columnDefinition = "mediumtext")
    private String xmlPhatHanh;

    protected HoaDon() {}

    public HoaDon(DonHang don, HoaDon truoc, Loai loai, String nguoiLap) {
        this.donId = don.getIdDonHang();
        this.maDon = don.getMaDon();
        this.loai = loai;
        this.nguoiLap = nguoiLap;
        ngayLap = bayGio();
        lanLap = truoc == null ? 1 : truoc.lanLap + 1;
        if (truoc == null) {
            nguoiMua = don.getHoTenNhan();
            diaChi = don.getDiaChi();
        } else {
            banTruocId = truoc.id;
            soThamChieu = truoc.soHoaDon;
            nguoiMua = truoc.nguoiMua;
            diaChi = truoc.diaChi;
            mst = truoc.mst;
            thayDong(truoc.dongs, truoc.giam);
        }
    }

    private static LocalDateTime bayGio() { return LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")); }
    public void thayDong(List<DongHoaDon> hang, BigDecimal giam) {
        dongs.clear();
        dongs.addAll(hang);
        this.giam = giam;
    }
    public void suaThongTin(String ten, String dc, String mst, String lyDo) {
        nguoiMua = ten;
        diaChi = dc;
        this.mst = mst;
        this.lyDo = lyDo;
        lanSua++;
    }
    public void capSo(String nguoi) {
        ngayPhatHanh = bayGio();
        soHoaDon = "DEMO-" + ngayPhatHanh.getYear() + "-" + String.format(java.util.Locale.ROOT, "%08d", id);
        nguoiPhatHanh = nguoi;
    }
    public void luuXml(String xml) { xmlPhatHanh = xml; }
    public void biThayThe(String soMoi) { thayBoi = soMoi; }
    public BigDecimal getTong() {
        return dongs.stream().map(DongHoaDon::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(giam);
    }
    public String getTrangThai() {
        if (thayBoi != null) return "Đã thay thế";
        return isNhap() ? "Bản nháp" : "Đã phát hành mô phỏng";
    }
    public boolean isNhap() { return soHoaDon == null; }
    public Long getId() { return id; }
    public int getDonId() { return donId; }
    public String getMaDon() { return maDon; }
    public int getLanLap() { return lanLap; }
    public long getLanSua() { return lanSua; }
    public Loai getLoai() { return loai; }
    public Long getBanTruocId() { return banTruocId; }
    public String getSoThamChieu() { return soThamChieu; }
    public String getSoHoaDon() { return soHoaDon; }
    public String getThayBoi() { return thayBoi; }
    public String getNguoiMua() { return nguoiMua; }
    public String getDiaChi() { return diaChi; }
    public String getMst() { return mst; }
    public String getLyDo() { return lyDo; }
    public String getNguoiLap() { return nguoiLap; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public String getNguoiPhatHanh() { return nguoiPhatHanh; }
    public LocalDateTime getNgayPhatHanh() { return ngayPhatHanh; }
    public BigDecimal getGiam() { return giam; }
    public List<DongHoaDon> getDongs() { return dongs; }
    public String getXmlPhatHanh() { return xmlPhatHanh; }
}

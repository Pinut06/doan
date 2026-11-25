package com.example.phukien.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "don_hang")
public class DonHang {

    @Version
    @org.hibernate.annotations.ColumnDefault("0")
    @Column(name = "phien_ban", nullable = false)
    private long phienBan;

    @Column(name = "ma_yeu_cau", unique = true, length = 40)
    private String maYeuCau;

    @Column(name = "nhan_vien_ban", length = 50)
    private String nhanVienBan;
    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    public void ghiNhanNguoiBan(String tenDangNhap) {
        if (nhanVienBan == null) nhanVienBan = tenDangNhap;
    }
    public void chotBanHang(LocalDateTime luc) {
        ngayHoanThanh = luc;
        chiTiets.forEach(ChiTietDonHang::chotGiaVon);
    }

    public long getPhienBan() { return phienBan; }
    public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "don_hang_id")
    private Integer idDonHang;

    @Column(name = "ma_don", length = 30)
    private String maDon;

    @ManyToOne(optional = false)
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;

    @Column(name = "ho_ten_nhan", nullable = false)
    private String hoTenNhan;

    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String soDienThoai;

    @Column(name = "dia_chi", nullable = false, length = 500)
    private String diaChi;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc", nullable = false, length = 20)
    private PhuongThucThanhToan phuongThuc = PhuongThucThanhToan.COD;

    @Column(name = "da_thanh_toan")
    private Boolean daThanhToan = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiDon trangThai = TrangThaiDon.CHO_XAC_NHAN;

    @Column(name = "dat_truoc")
    private Boolean datTruoc = false;

    @Column(name = "tong_tien", precision = 15, scale = 0)
    private BigDecimal tongTien = BigDecimal.ZERO;

    // NULL identifies orders created before the payment ledger was introduced.
    @Column(name = "tong_thu", precision = 19, scale = 0)
    private BigDecimal tongThu = BigDecimal.ZERO;

    @Column(name = "tong_hoan", precision = 19, scale = 0)
    private BigDecimal tongHoan = BigDecimal.ZERO;

    @Column(name = "giam_sau_ban", precision = 15, scale = 0)
    private BigDecimal giamSauBan = BigDecimal.ZERO;

    public boolean canChuyenSoDuCu() { return tongThu == null; }
    public BigDecimal getTongThu() {
        if (tongThu != null) return tongThu;
        return Boolean.TRUE.equals(daThanhToan) && tongTien != null ? tongTien : BigDecimal.ZERO;
    }
    public BigDecimal getTongHoan() { return tongHoan == null ? BigDecimal.ZERO : tongHoan; }
    public BigDecimal getGiamSauBan() { return giamSauBan == null ? BigDecimal.ZERO : giamSauBan; }
    public BigDecimal getThucThu() { return getTongThu().subtract(getTongHoan()); }
    public BigDecimal getGiaTriPhaiThu() {
        return trangThai == TrangThaiDon.DA_HUY || tongTien == null ? BigDecimal.ZERO : tongTien.subtract(getGiamSauBan());
    }
    public BigDecimal getConNo() { return getGiaTriPhaiThu().subtract(getThucThu()).max(BigDecimal.ZERO); }
    public BigDecimal getCanHoan() { return getThucThu().subtract(getGiaTriPhaiThu()).max(BigDecimal.ZERO); }
    public BigDecimal getGioiHanHoan() {
        return trangThai == TrangThaiDon.HOAN_THANH ? getThucThu() : getCanHoan();
    }
    public String getNhanThanhToan() {
        if (getCanHoan().signum() > 0) return "Còn tiền phải hoàn";
        if (getTongHoan().signum() > 0 && getThucThu().signum() == 0)
            return getConNo().signum() > 0 ? "Đã hoàn tiền, còn phải thu" : "Đã hoàn tiền";
        if (trangThai == TrangThaiDon.DA_HUY) return "Đã hủy";
        if (getConNo().signum() == 0) {
            if (getTongHoan().signum() > 0 && getGiamSauBan().signum() > 0) return "Đã hoàn một phần";
            return getGiamSauBan().signum() > 0 ? "Đã tất toán sau điều chỉnh" : "Đã thanh toán";
        }
        return getThucThu().signum() > 0 ? "Đã thanh toán một phần" : "Chưa thanh toán";
    }

    public void khoiTaoSoDu() {
        tongThu = getTongThu();
        tongHoan = getTongHoan();
        giamSauBan = getGiamSauBan();
    }

    public void ghiNhanPhieu(PhieuThanhToan.Loai loai, BigDecimal tien, BigDecimal giam) {
        if (loai == PhieuThanhToan.Loai.THU) tongThu = getTongThu().add(tien);
        else tongHoan = getTongHoan().add(tien);
        giamSauBan = getGiamSauBan().add(giam);
        daThanhToan = trangThai != TrangThaiDon.DA_HUY && getConNo().signum() == 0;
    }

    public void ghiGiamHangTra(BigDecimal giaTri) {
        giamSauBan = getGiamSauBan().add(giaTri);
        daThanhToan = getConNo().signum() == 0;
    }

    @Embedded
    private UuDaiDon uuDai;

    public UuDaiDon getUuDai() { return uuDai; }
    public void setUuDai(UuDaiDon uuDai) { this.uuDai = uuDai; }

    public void tinhTongPhaiTra(BigDecimal tienHang) {
        tongTien = uuDai == null ? tienHang : uuDai.tinhLai(tienHang);
        if (tongTien.signum() == 0) daThanhToan = true;
    }

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ChiTietDonHang> chiTiets = new ArrayList<>();

    public DonHang() {
    }

    public void themChiTiet(ChiTietDonHang ct) {
        ct.setDonHang(this);
        chiTiets.add(ct);
    }

    public boolean choPhepHuy() {
        return trangThai == TrangThaiDon.CHO_XAC_NHAN || trangThai == TrangThaiDon.CHO_HANG;
    }

    public boolean isChoSuaGiaoHang() {
        return choPhepHuy() || trangThai == TrangThaiDon.DA_XAC_NHAN;
    }

    public boolean isChoSuaSoLuong() {
        return choPhepHuy() && !Boolean.TRUE.equals(daThanhToan) && getTongThu().signum() == 0;
    }

    public boolean isCanHoanTien() {
        return trangThai == TrangThaiDon.DA_HUY && getCanHoan().signum() > 0;
    }

    public Integer getIdDonHang() { return idDonHang; }
    public void setIdDonHang(Integer idDonHang) { this.idDonHang = idDonHang; }
    public String getMaDon() { return maDon; }
    public void setMaDon(String maDon) { this.maDon = maDon; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public String getHoTenNhan() { return hoTenNhan; }
    public void setHoTenNhan(String hoTenNhan) { this.hoTenNhan = hoTenNhan; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public PhuongThucThanhToan getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(PhuongThucThanhToan phuongThuc) { this.phuongThuc = phuongThuc; }
    public Boolean getDaThanhToan() { return daThanhToan; }
    public void setDaThanhToan(Boolean daThanhToan) { this.daThanhToan = daThanhToan; }
    public TrangThaiDon getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDon trangThai) { this.trangThai = trangThai; }
    public Boolean getDatTruoc() { return datTruoc; }
    public void setDatTruoc(Boolean datTruoc) { this.datTruoc = datTruoc; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public List<ChiTietDonHang> getChiTiets() { return chiTiets; }
    public void setChiTiets(List<ChiTietDonHang> chiTiets) { this.chiTiets = chiTiets; }
}

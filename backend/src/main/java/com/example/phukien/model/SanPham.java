package com.example.phukien.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "san_pham")
public class SanPham {
    private static final ZoneId MUI_GIO_CUA_HANG = ZoneId.of("Asia/Ho_Chi_Minh");

    @Version
    @org.hibernate.annotations.ColumnDefault("0")
    @Column(name = "phien_ban", nullable = false)
    private long phienBan;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.EAGER)
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @OrderBy("tuNgay desc, id desc")
    private List<KhuyenMai> lichKhuyenMai = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "san_pham_id")
    private Integer idSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Column(name = "ten_san_pham", nullable = false)
    private String tenSanPham;

    @Column(name = "ma_san_pham")
    private String maSanPham;

    @Column(name = "thuong_hieu")
    private String thuongHieu;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "noi_bat")
    private Boolean noiBat = false;

    @Column(name = "ban_chay")
    private Boolean banChay = false;

    @NotNull(message = "Vui lòng nhập giá")
    @DecimalMin(value = "0", message = "Giá không được âm")
    @Digits(integer = 15, fraction = 0, message = "Giá phải là số nguyên, tối đa 15 chữ số.")
    @Column(name = "gia_co_ban", precision = 15, scale = 0)
    private BigDecimal giaCoBan;

    @DecimalMin(value = "0", message = "Giá vốn không được âm")
    @Digits(integer = 15, fraction = 0, message = "Giá vốn phải là số nguyên, tối đa 15 chữ số.")
    @Column(name = "gia_von", precision = 15, scale = 0)
    private BigDecimal giaVon;

    public BigDecimal getGiaVon() { return giaVon; }
    public void setGiaVon(BigDecimal giaVon) { this.giaVon = giaVon; }

    @NotNull(message = "Vui lòng nhập số lượng tồn")
    @Min(value = 0, message = "Số lượng tồn không được âm")
    @Column(name = "so_luong_ton")
    private Integer soLuongTon = 0;

    @Column(name = "bao_hanh_thang")
    private Integer baoHanhThang;

    @Column(name = "dat_truoc")
    private Boolean datTruoc = false;

    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_du_kien")
    private LocalDate ngayDuKien;

    @ManyToOne
    @JoinColumn(name = "danh_muc_id")
    private DanhMuc danhMuc;

    public SanPham() {
    }

    public long getPhienBan() { return phienBan; }
    public void setPhienBan(long phienBan) { this.phienBan = phienBan; }
    public List<KhuyenMai> getLichKhuyenMai() { return lichKhuyenMai; }
    public static LocalDate ngayBan() { return LocalDate.now(MUI_GIO_CUA_HANG); }

    private KhuyenMai khuyenMaiNgay(LocalDate ngay) {
        return lichKhuyenMai.stream().filter(k -> k.apDung(ngay)).findFirst().orElse(null);
    }

    public KhuyenMai getKhuyenMaiHienTai() { return khuyenMaiNgay(ngayBan()); }

    public BigDecimal giaBanNgay(LocalDate ngay) {
        KhuyenMai km = khuyenMaiNgay(ngay);
        if (km == null || giaCoBan == null) return giaCoBan;
        return giaCoBan.multiply(BigDecimal.valueOf(100 - km.getPhanTram()))
                .movePointLeft(2).setScale(0, RoundingMode.HALF_UP);
    }

    public BigDecimal getGiaBan() { return giaBanNgay(ngayBan()); }


    public boolean isHetHang() {
        return soLuongTon == null || soLuongTon <= 0;
    }

    public boolean isDatTruocDuoc() {
        return isHetHang() && Boolean.TRUE.equals(datTruoc);
    }

    public List<String[]> getThongSo() {
        List<String[]> kq = new ArrayList<>();
        for (String dong : cacDongMoTa()) {
            String[] kv = tachThongSo(dong);
            if (kv != null) {
                kq.add(kv);
            }
        }
        return kq;
    }

    public List<String> getDiemNoiBat() {
        List<String> kq = new ArrayList<>();
        for (String dong : cacDongMoTa()) {
            if (tachThongSo(dong) == null) {
                kq.add(dong);
            }
        }
        return kq;
    }

    private List<String> cacDongMoTa() {
        List<String> kq = new ArrayList<>();
        if (moTa == null) {
            return kq;
        }
        for (String dong : moTa.split("\\r?\\n")) {
            String d = dong.trim();
            if (!d.isEmpty()) {
                kq.add(d);
            }
        }
        return kq;
    }

    private static String[] tachThongSo(String dong) {
        int i = dong.indexOf(':');
        if (i < 0) {
            i = dong.indexOf('\t');
        }
        if (i > 0 && i <= 30 && i < dong.length() - 1) {
            String k = dong.substring(0, i).trim();
            String v = dong.substring(i + 1).trim();
            if (!k.isEmpty() && !v.isEmpty()) {
                return new String[] { k, v };
            }
        }
        return null;
    }

    public Integer getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(Integer idSanPham) {
        this.idSanPham = idSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public Boolean getNoiBat() {
        return noiBat;
    }

    public void setNoiBat(Boolean noiBat) {
        this.noiBat = noiBat;
    }

    public Boolean getBanChay() {
        return banChay;
    }

    public void setBanChay(Boolean banChay) {
        this.banChay = banChay;
    }

    public BigDecimal getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(BigDecimal giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public Integer getBaoHanhThang() {
        return baoHanhThang;
    }

    public void setBaoHanhThang(Integer baoHanhThang) {
        this.baoHanhThang = baoHanhThang;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public Boolean getDatTruoc() {
        return datTruoc;
    }

    public void setDatTruoc(Boolean datTruoc) {
        this.datTruoc = datTruoc;
    }

    public LocalDate getNgayDuKien() {
        return ngayDuKien;
    }

    public void setNgayDuKien(LocalDate ngayDuKien) {
        this.ngayDuKien = ngayDuKien;
    }
}

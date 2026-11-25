package com.example.phukien.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import jakarta.persistence.*;

@Entity
@Table(name = "yeu_cau_doi_tra")
public class YeuCauDoiTra {
    public enum Loai {
        TRA_HOAN_TIEN("Trả hàng và hoàn tiền"), DOI_SAN_PHAM("Đổi sản phẩm");
        private final String nhan;
        Loai(String nhan) { this.nhan = nhan; }
        public String getNhan() { return nhan; }
    }
    public enum Buoc {
        TIEP_NHAN("Đã tiếp nhận"), DA_KIEM_TRA("Đã kiểm tra hàng"), DA_DUYET("Đã duyệt"),
        TU_CHOI("Đã từ chối"), HOAN_TAT("Hoàn tất");
        private final String nhan;
        Buoc(String nhan) { this.nhan = nhan; }
        public String getNhan() { return nhan; }
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "chi_tiet_id")
    private ChiTietDonHang chiTiet;
    @Column(nullable = false, unique = true, length = 36)
    private String maGui;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Loai loai;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Buoc buoc;
    @Column(nullable = false)
    private int soLuong;
    @Column(nullable = false, length = 500)
    private String lyDo;
    @Column(nullable = false)
    private String nguoiTao;
    @Column(nullable = false)
    private LocalDateTime ngayTao;
    @Column(length = 500)
    private String ketQuaKiem;
    private Integer soBanLai;
    private String nguoiKiem;
    private LocalDateTime ngayKiem;
    @Column(length = 500)
    private String ghiChuDuyet;
    private String nguoiDuyet;
    private LocalDateTime ngayDuyet;
    private String nguoiXuLy;
    private LocalDateTime ngayXuLy;
    @Column(precision = 15, scale = 0)
    private BigDecimal giaTriTra;
    @OneToOne @JoinColumn(name = "phieu_hoan_id", unique = true)
    private PhieuThanhToan phieuHoan;
    @ManyToOne @JoinColumn(name = "san_pham_doi_id")
    private SanPham sanPhamDoi;
    @Column(name = "gia_von_hang_doi", precision = 15, scale = 0)
    private BigDecimal giaVonHangDoi;

    protected YeuCauDoiTra() {}
    public YeuCauDoiTra(ChiTietDonHang chiTiet, String maGui, Loai loai, int soLuong, String lyDo, String nguoi) {
        this.chiTiet = chiTiet;
        this.maGui = maGui;
        this.loai = loai;
        this.soLuong = soLuong;
        this.lyDo = lyDo;
        nguoiTao = nguoi;
        ngayTao = bayGio();
        buoc = Buoc.TIEP_NHAN;
    }
    private static LocalDateTime bayGio() { return LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")); }

    public void kiem(String ketQua, int banLai, String nguoi) {
        ketQuaKiem = ketQua;
        soBanLai = banLai;
        nguoiKiem = nguoi;
        ngayKiem = bayGio();
        buoc = Buoc.DA_KIEM_TRA;
    }
    public void quyetDinh(boolean duyet, String ghiChu, String nguoi) {
        ghiChuDuyet = ghiChu;
        nguoiDuyet = nguoi;
        ngayDuyet = bayGio();
        buoc = duyet ? Buoc.DA_DUYET : Buoc.TU_CHOI;
    }
    public void ketThuc(BigDecimal giaTri, PhieuThanhToan phieu, SanPham doi, String nguoi) {
        giaTriTra = giaTri;
        phieuHoan = phieu;
        sanPhamDoi = doi;
        giaVonHangDoi = doi == null ? null : doi.getGiaVon();
        nguoiXuLy = nguoi;
        ngayXuLy = bayGio();
        buoc = Buoc.HOAN_TAT;
    }

    public Long getId() { return id; }
    public ChiTietDonHang getChiTiet() { return chiTiet; }
    public String getMaGui() { return maGui; }
    public Loai getLoai() { return loai; }
    public Buoc getBuoc() { return buoc; }
    public int getSoLuong() { return soLuong; }
    public String getLyDo() { return lyDo; }
    public String getNguoiTao() { return nguoiTao; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public String getKetQuaKiem() { return ketQuaKiem; }
    public Integer getSoBanLai() { return soBanLai; }
    public String getNguoiKiem() { return nguoiKiem; }
    public LocalDateTime getNgayKiem() { return ngayKiem; }
    public String getGhiChuDuyet() { return ghiChuDuyet; }
    public String getNguoiDuyet() { return nguoiDuyet; }
    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public String getNguoiXuLy() { return nguoiXuLy; }
    public LocalDateTime getNgayXuLy() { return ngayXuLy; }
    public BigDecimal getGiaTriTra() { return giaTriTra; }
    public PhieuThanhToan getPhieuHoan() { return phieuHoan; }
    public SanPham getSanPhamDoi() { return sanPhamDoi; }
}

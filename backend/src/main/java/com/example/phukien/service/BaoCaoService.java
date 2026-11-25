package com.example.phukien.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.phukien.model.TrangThaiDon;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BaoCaoService {
    public enum Loai {
        DOANH_THU("doanh-thu", "Doanh thu",
                "Ghi nhận khi giao thành công. Giá trị bán đã trừ khuyến mãi. Hàng trả và giảm sau bán được ghi vào ngày xử lý.",
                "Ngày", "Đơn giao", "Giá trị bán (₫)", "Hàng trả (₫)", "Giảm khác (₫)", "Doanh thu thuần (₫)"),
        DON_HANG("don-hang", "Đơn hàng",
                "Các đơn tạo trong kỳ được nhóm theo trạng thái hiện tại. Giá trị đặt hàng gồm cả các đơn đã hủy.",
                "Trạng thái hiện tại", "Số đơn", "Giá trị đặt hàng (₫)"),
        SAN_PHAM("san-pham", "Sản phẩm bán chạy",
                "Xếp theo số lượng giao thành công trừ số đã trả hoàn tiền trong kỳ. Hàng đổi ngang không tạo lượt bán mới.",
                "Sản phẩm", "Số lượng giao", "Số lượng trả", "Số lượng bán ròng"),
        CONG_NO("cong-no", "Công nợ",
                "Số dư hiện tại theo khách hàng. Tính số phải thu và phải hoàn riêng trên từng đơn rồi cộng theo khách.",
                "Khách hàng", "Đơn còn số dư", "Còn phải thu (₫)", "Còn phải hoàn (₫)"),
        LOI_NHUAN("loi-nhuan", "Lợi nhuận gộp",
                "Lãi gộp bằng doanh thu thuần trừ giá vốn hàng bán và hàng xuất đổi, cộng giá vốn hàng nhập lại để bán. Chi phí vận hành chưa được thu thập.",
                "Ngày", "Doanh thu thuần (₫)", "Giá vốn xuất đã biết (₫)", "Giá vốn nhập lại đã biết (₫)", "Lượt sản phẩm thiếu giá vốn", "Lợi nhuận gộp (₫)"),
        NHAN_VIEN("nhan-vien", "Hiệu quả nhân viên bán hàng",
                "Đơn tại quầy thuộc người tạo đơn, đơn trực tuyến thuộc người xác nhận đơn. Đơn tạo và đơn giao được đếm theo ngày phát sinh tương ứng.",
                "Tài khoản phụ trách", "Đơn tạo trong kỳ", "Đơn giao trong kỳ", "Giá trị bán (₫)", "Giảm sau bán (₫)", "Doanh thu thuần (₫)", "Giá trị bán TB/đơn giao (₫)");

        private final String ma, ten, moTa;
        private final List<String> cot;
        Loai(String ma, String ten, String moTa, String... cot) {
            this.ma = ma;
            this.ten = ten;
            this.moTa = moTa;
            this.cot = List.of(cot);
        }
        public String getMa() { return ma; }
        public String getTen() { return ten; }
        public String getMoTa() { return moTa; }
        public List<String> getCot() { return cot; }
    }

    private final NamedParameterJdbcTemplate db;
    public BaoCaoService(NamedParameterJdbcTemplate db) { this.db = db; }

    // Mỗi nhánh phát sinh đúng một lần theo ngày nghiệp vụ, kể cả trả hàng của kỳ trước.
    private static final String BAN_HANG = """
            WITH ban AS (
              SELECT DATE(d.ngay_hoan_thanh) ngay, d.nhan_vien_ban nv, 1 don,
                     d.tong_tien tien, 0 tra, 0 giam
              FROM don_hang d
              WHERE d.trang_thai='HOAN_THANH' AND d.ngay_hoan_thanh >= :tu AND d.ngay_hoan_thanh < :het
              UNION ALL
              SELECT DATE(y.ngay_xu_ly), d.nhan_vien_ban, 0, 0, y.gia_tri_tra, 0
              FROM yeu_cau_doi_tra y JOIN chi_tiet_don_hang c ON c.chi_tiet_id=y.chi_tiet_id
              JOIN don_hang d ON d.don_hang_id=c.don_hang_id
              WHERE y.buoc='HOAN_TAT' AND y.loai='TRA_HOAN_TIEN' AND y.ngay_xu_ly >= :tu AND y.ngay_xu_ly < :het
              UNION ALL
              SELECT DATE(p.ngay_lap), d.nhan_vien_ban, 0, 0, 0, p.giam_sau_ban
              FROM phieu_thanh_toan p JOIN don_hang d ON d.don_hang_id=p.don_hang_id
              WHERE p.loai='HOAN' AND p.giam_sau_ban > 0 AND p.ngay_lap >= :tu AND p.ngay_lap < :het
            )
            """;
    private static final String DOANH_THU = BAN_HANG + """
            SELECT ngay, SUM(don), SUM(tien), SUM(tra), SUM(giam), SUM(tien-tra-giam)
            FROM ban GROUP BY ngay WITH ROLLUP
            """;
    private static final String NHAN_VIEN = BAN_HANG + """
            , theo_nv AS (
              SELECT COALESCE(nv,'Chưa ghi nhận') nv, 0 tao, don, tien, tra+giam giam FROM ban
              UNION ALL
              SELECT COALESCE(nhan_vien_ban,'Chưa ghi nhận'), 1, 0, 0, 0 FROM don_hang
              WHERE ngay_tao >= :tu AND ngay_tao < :het
            )
            SELECT * FROM (
              SELECT nv, SUM(tao) tao, SUM(don) giao, SUM(tien) tien, SUM(giam) giam, SUM(tien-giam) thuan,
                     ROUND(SUM(tien)/NULLIF(SUM(don),0)) trung_binh
              FROM theo_nv GROUP BY nv WITH ROLLUP
            ) r ORDER BY nv IS NULL, thuan DESC, nv
            """;
    private static final String DON_HANG = """
            SELECT trang_thai, COUNT(*), SUM(tong_tien) FROM don_hang
            WHERE ngay_tao >= :tu AND ngay_tao < :het GROUP BY trang_thai WITH ROLLUP
            """;
    private static final String SAN_PHAM = """
            WITH luong AS (
              SELECT c.san_pham_id sp, c.so_luong giao, 0 tra
              FROM don_hang d JOIN chi_tiet_don_hang c ON c.don_hang_id=d.don_hang_id
              WHERE d.trang_thai='HOAN_THANH' AND d.ngay_hoan_thanh >= :tu AND d.ngay_hoan_thanh < :het
              UNION ALL
              SELECT c.san_pham_id, 0, y.so_luong FROM yeu_cau_doi_tra y
              JOIN chi_tiet_don_hang c ON c.chi_tiet_id=y.chi_tiet_id
              WHERE y.buoc='HOAN_TAT' AND y.loai='TRA_HOAN_TIEN' AND y.ngay_xu_ly >= :tu AND y.ngay_xu_ly < :het
            ), xep AS (
              SELECT sp, SUM(giao) giao, SUM(tra) tra, SUM(giao-tra) rong FROM luong GROUP BY sp WITH ROLLUP
            )
            SELECT CASE WHEN x.sp IS NOT NULL THEN CONCAT_WS(' · ',s.ma_san_pham,s.ten_san_pham) END, x.giao,x.tra,x.rong
            FROM xep x LEFT JOIN san_pham s ON s.san_pham_id=x.sp
            ORDER BY x.sp IS NULL, x.rong DESC, x.giao DESC, x.sp
            """;
    private static final String CONG_NO = """
            WITH so_du AS (
              SELECT khach_hang_id kh,
                CASE WHEN trang_thai='DA_HUY' OR tong_tien IS NULL THEN 0 ELSE tong_tien-COALESCE(giam_sau_ban,0) END
                - COALESCE(tong_thu, CASE WHEN da_thanh_toan=1 THEN COALESCE(tong_tien,0) ELSE 0 END)
                + COALESCE(tong_hoan,0) du
              FROM don_hang
            ), theo_khach AS (
              SELECT kh, COUNT(*) don, SUM(GREATEST(du,0)) no, SUM(GREATEST(-du,0)) hoan
              FROM so_du WHERE du<>0 GROUP BY kh WITH ROLLUP
            )
            SELECT CASE WHEN kh IS NOT NULL THEN CONCAT_WS(' · ',NULLIF(k.ho_ten,''),k.ten_dang_nhap) END,
                   don,no,hoan FROM theo_khach LEFT JOIN khach_hang k ON k.khach_hang_id=kh
            ORDER BY kh IS NULL, no DESC, hoan DESC, kh
            """;
    private static final String LOI_NHUAN = BAN_HANG + """
            , chi_phi AS (
              SELECT DATE(d.ngay_hoan_thanh) ngay, c.so_luong * COALESCE(c.gia_von_khi_ban,0) xuat,
                     0 nhap, CASE WHEN c.gia_von_khi_ban IS NULL THEN c.so_luong ELSE 0 END thieu
              FROM don_hang d JOIN chi_tiet_don_hang c ON c.don_hang_id=d.don_hang_id
              WHERE d.trang_thai='HOAN_THANH' AND d.ngay_hoan_thanh >= :tu AND d.ngay_hoan_thanh < :het
              UNION ALL
              SELECT DATE(y.ngay_xu_ly),
                     CASE WHEN y.loai='DOI_SAN_PHAM' THEN y.so_luong * COALESCE(y.gia_von_hang_doi,0) ELSE 0 END,
                     y.so_ban_lai * COALESCE(c.gia_von_khi_ban,0),
                     CASE WHEN y.loai='DOI_SAN_PHAM' AND y.gia_von_hang_doi IS NULL THEN y.so_luong ELSE 0 END
                     + CASE WHEN c.gia_von_khi_ban IS NULL THEN y.so_ban_lai ELSE 0 END
              FROM yeu_cau_doi_tra y JOIN chi_tiet_don_hang c ON c.chi_tiet_id=y.chi_tiet_id
              WHERE y.buoc='HOAN_TAT' AND y.ngay_xu_ly >= :tu AND y.ngay_xu_ly < :het
            ), phat_sinh AS (
              SELECT ngay, tien-tra-giam thuan, 0 xuat, 0 nhap, 0 thieu FROM ban
              UNION ALL SELECT ngay, 0, xuat, nhap, thieu FROM chi_phi
            )
            SELECT ngay, SUM(thuan), SUM(xuat), SUM(nhap), SUM(thieu),
                   CASE WHEN SUM(thieu)=0 THEN SUM(thuan-xuat+nhap) END
            FROM phat_sinh GROUP BY ngay WITH ROLLUP
            """;

    public record Dong(String nhan, List<String> soLieu, boolean tong) {}
    public record KetQua(List<Dong> dongs, long thieuNgayGiao) {}

    public KetQua xem(Loai loai, LocalDate tu, LocalDate den) {
        var ky = new MapSqlParameterSource().addValue("tu", tu.atStartOfDay()).addValue("het", den.plusDays(1).atStartOfDay());
        String sql = switch (loai) {
            case DOANH_THU -> DOANH_THU;
            case DON_HANG -> DON_HANG;
            case SAN_PHAM -> SAN_PHAM;
            case CONG_NO -> CONG_NO;
            case LOI_NHUAN -> LOI_NHUAN;
            case NHAN_VIEN -> NHAN_VIEN;
        };
        var dinhDang = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
        var ds = db.query(sql, ky, (rs, i) -> {
            String nhan = rs.getString(1);
            boolean tong = nhan == null;
            if (tong) nhan = "Tổng cộng";
            else if (loai == Loai.DON_HANG) nhan = TrangThaiDon.valueOf(nhan).getNhan();
            else if (loai == Loai.DOANH_THU || loai == Loai.LOI_NHUAN)
                nhan = rs.getDate(1).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            List<String> o = new ArrayList<>();
            for (int cot = 2; cot <= loai.getCot().size(); cot++) {
                var so = rs.getBigDecimal(cot);
                o.add(so != null ? dinhDang.format(so) : loai == Loai.LOI_NHUAN ? "Chưa đủ giá vốn" : "—");
            }
            return new Dong(nhan, o, tong);
        });
        long thieuNgay = switch (loai) {
            case DON_HANG, CONG_NO -> 0L;
            default -> db.getJdbcTemplate().queryForObject(
                    "SELECT COUNT(*) FROM don_hang WHERE trang_thai='HOAN_THANH' AND ngay_hoan_thanh IS NULL", Long.class);
        };
        return new KetQua(ds, thieuNgay);
    }
}

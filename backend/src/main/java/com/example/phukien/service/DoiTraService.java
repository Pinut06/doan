package com.example.phukien.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import com.example.phukien.model.*;
import com.example.phukien.model.YeuCauDoiTra.Buoc;
import com.example.phukien.model.YeuCauDoiTra.Loai;
import com.example.phukien.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class DoiTraService {
    private final DonHangRepository dons;
    private final YeuCauDoiTraRepository yeuCaus;
    private final SanPhamRepository kho;
    private final ThanhToanService thanhToan;

    public DoiTraService(DonHangRepository dons, YeuCauDoiTraRepository yeuCaus,
                        SanPhamRepository kho, ThanhToanService thanhToan) {
        this.dons = dons;
        this.yeuCaus = yeuCaus;
        this.kho = kho;
        this.thanhToan = thanhToan;
    }

    public record Dong(ChiTietDonHang hang, int conLai) {}
    public record Tien(BigDecimal giaTri, BigDecimal thucHoan) {}
    public record HoSo(DonHang don, List<Dong> dongs, List<YeuCauDoiTra> yeuCaus,
                       Map<Long,Tien> duTinh, Map<Long,List<SanPham>> hangDoi) {}

    public Page<YeuCauDoiTra> traCuu(String q, Buoc buoc, int trang) {
        return yeuCaus.traCuu(q.strip(), buoc, PageRequest.of(Math.max(0, trang), 25));
    }

    private DonHang don(int id, String khach, boolean khoa) {
        var don = (khoa ? dons.findByIdForUpdate(id) : dons.findById(id)).orElseThrow(DoiTraService::khongThay);
        if (khach != null && !khach.equals(don.getKhachHang().getTenDangNhap())) throw khongThay();
        return don;
    }
    private static ResponseStatusException khongThay() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }

    public HoSo xem(int id, String khach) {
        DonHang don = don(id, khach, false);
        var ds = yeuCaus.findByChiTiet_DonHang_IdDonHangOrderByIdDesc(id);
        var dongs = don.getChiTiets().stream().map(c -> new Dong(c, conLai(c, ds))).filter(d -> d.conLai() > 0).toList();
        Map<Long,Tien> duTinh = new HashMap<>();
        Map<Long,List<SanPham>> hangDoi = new HashMap<>();
        if (khach == null) {
            List<SanPham> dangBan = null;
            for (var y : ds) {
                if (y.getBuoc() != Buoc.DA_DUYET) continue;
                if (y.getLoai() == Loai.TRA_HOAN_TIEN) duTinh.put(y.getId(), tinhTien(don, y, ds));
                else {
                    if (dangBan == null) dangBan = kho.traCuu(null, "", true, "con");
                    hangDoi.put(y.getId(), dangBan.stream().filter(sp -> choDoi(y, sp)
                            && sp.getSoLuongTon() >= y.getSoLuong()).toList());
                }
            }
        }
        return new HoSo(don, dongs, ds, duTinh, hangDoi);
    }

    private int conLai(ChiTietDonHang c, List<YeuCauDoiTra> ds) {
        return c.getSoLuong() - ds.stream().filter(y -> y.getChiTiet().getIdChiTiet().equals(c.getIdChiTiet())
                && y.getBuoc() != Buoc.TU_CHOI).mapToInt(YeuCauDoiTra::getSoLuong).sum();
    }

    @Transactional
    public void tiepNhan(int id, String khach, String nguoi, UUID ma, int dong, int sl, Loai loai, String lyDo) {
        var don = don(id, khach, true);
        if (don.getTrangThai() != TrangThaiDon.HOAN_THANH) throw new IllegalStateException("Chỉ tiếp nhận hàng của đơn đã giao thành công.");
        var ct = don.getChiTiets().stream().filter(c -> c.getIdChiTiet() == dong).findFirst().orElseThrow(DoiTraService::khongThay);
        String ghiChu = noiDung(lyDo, "lý do đổi trả");
        var cu = yeuCaus.findByMaGui(ma.toString());
        if (cu.isPresent()) {
            var y = cu.get();
            if (y.getChiTiet().getIdChiTiet() == dong && y.getSoLuong() == sl && y.getLoai() == loai
                    && y.getLyDo().equals(ghiChu) && y.getNguoiTao().equals(nguoi)) return;
            throw new IllegalStateException("Mẫu yêu cầu đã được sử dụng. Vui lòng tải lại.");
        }
        if (sl < 1 || sl > conLai(ct, yeuCaus.findByChiTiet_DonHang_IdDonHangOrderByIdDesc(id)))
            throw new IllegalStateException("Số lượng vượt số hàng còn được đổi trả.");
        yeuCaus.save(new YeuCauDoiTra(ct, ma.toString(), loai, sl, ghiChu, nguoi));
    }

    public enum ThaoTac { KIEM, DUYET, TU_CHOI, HOAN_TIEN, DOI_HANG }
    public record Lenh(ThaoTac thaoTac, Buoc buocCu, String ghiChu, Integer soBanLai, Integer spDoi,
                       BigDecimal giaTri, BigDecimal tienHoan, PhieuThanhToan.HinhThuc hinhThuc,
                       String thamChieu, String nguoiNhan) {}

    @Transactional
    public void xuLy(int donId, long id, Lenh lenh, String nguoi) {
        var don = don(donId, null, true);
        var ds = yeuCaus.findByChiTiet_DonHang_IdDonHangOrderByIdDesc(donId);
        var y = ds.stream().filter(item -> item.getId() == id).findFirst().orElseThrow(DoiTraService::khongThay);
        if (y.getBuoc() != lenh.buocCu()) throw new IllegalStateException("Hồ sơ đã thay đổi, vui lòng kiểm tra lại.");
        switch (lenh.thaoTac()) {
            case KIEM -> {
                canBuoc(y, Buoc.TIEP_NHAN);
                if (lenh.soBanLai() == null || lenh.soBanLai() < 0 || lenh.soBanLai() > y.getSoLuong())
                    throw new IllegalStateException("Kiểm tra lại số lượng có thể bán lại.");
                y.kiem(noiDung(lenh.ghiChu(), "kết quả kiểm tra"), lenh.soBanLai(), nguoi);
            }
            case DUYET -> {
                canBuoc(y, Buoc.DA_KIEM_TRA);
                y.quyetDinh(true, null, nguoi);
            }
            case TU_CHOI -> {
                if (y.getBuoc() != Buoc.TIEP_NHAN) canBuoc(y, Buoc.DA_KIEM_TRA);
                y.quyetDinh(false, noiDung(lenh.ghiChu(), "lý do từ chối và hướng trả hàng cho khách"), nguoi);
            }
            case HOAN_TIEN, DOI_HANG -> {
                canBuoc(y, Buoc.DA_DUYET);
                if (lenh.thaoTac() == ThaoTac.HOAN_TIEN && y.getLoai() == Loai.TRA_HOAN_TIEN) {
                    var tien = tinhTien(don, y, ds);
                    if (!bangTien(lenh.giaTri(), tien.giaTri()) || !bangTien(lenh.tienHoan(), tien.thucHoan()))
                        throw new IllegalStateException("Giá trị hoặc số tiền hoàn đã thay đổi. Vui lòng đối chiếu lại.");
                    var phieu = thanhToan.hoanHang(don, id, tien.giaTri(), lenh.hinhThuc(), lenh.thamChieu(), lenh.nguoiNhan(), nguoi);
                    y.ketThuc(tien.giaTri(), phieu, null, nguoi);
                } else if (lenh.thaoTac() == ThaoTac.DOI_HANG && y.getLoai() == Loai.DOI_SAN_PHAM) {
                    if (lenh.spDoi() == null) throw new IllegalStateException("Chọn sản phẩm giao cho khách.");
                    var sp = kho.findByIdForUpdate(lenh.spDoi()).orElseThrow(DoiTraService::khongThay);
                    if (!choDoi(y, sp) || kho.giuTon(lenh.spDoi(), y.getSoLuong()) != 1)
                        throw new IllegalStateException("Hàng đổi phải còn đủ tồn và cùng mã hoặc cùng đơn giá mua.");
                    y.ketThuc(null, null, sp, nguoi);
                } else throw new IllegalStateException("Thao tác không phù hợp với loại yêu cầu.");
                if (y.getSoBanLai() > 0) kho.hoanTon(y.getChiTiet().getSanPham().getIdSanPham(), y.getSoBanLai());
            }
        }
    }

    private boolean choDoi(YeuCauDoiTra y, SanPham sp) {
        var ct = y.getChiTiet();
        return Boolean.TRUE.equals(sp.getTrangThai()) && (sp.getIdSanPham().equals(ct.getSanPham().getIdSanPham())
                || bangTien(sp.getGiaBan(), ct.getDonGia()));
    }

    private Tien tinhTien(DonHang don, YeuCauDoiTra y, List<YeuCauDoiTra> ds) {
        // Phân bổ giá trị còn phải thu sau mọi chiết khấu trên lượng hàng chưa ghi giảm.
        // Hiệu hai mốc lũy kế giữ phần làm tròn đến đồng khi trả theo nhiều lần.
        Map<Integer,Integer> daTra = new HashMap<>();
        for (var cu : ds) {
            if (cu.getBuoc() == Buoc.HOAN_TAT && cu.getLoai() == Loai.TRA_HOAN_TIEN)
                daTra.merge(cu.getChiTiet().getIdChiTiet(), cu.getSoLuong(), Integer::sum);
        }
        BigDecimal tong = BigDecimal.ZERO, truoc = BigDecimal.ZERO;
        int dong = y.getChiTiet().getIdChiTiet();
        for (var c : don.getChiTiets()) {
            var gia = c.getDonGia().multiply(BigDecimal.valueOf(c.getSoLuong() - daTra.getOrDefault(c.getIdChiTiet(), 0)));
            tong = tong.add(gia);
            if (c.getIdChiTiet() < dong) truoc = truoc.add(gia);
        }
        BigDecimal giaTri = BigDecimal.ZERO;
        if (tong.signum() > 0) {
            var muc = don.getGiaTriPhaiThu().min(tong);
            var hangTra = y.getChiTiet().getDonGia().multiply(BigDecimal.valueOf(y.getSoLuong()));
            giaTri = truoc.add(hangTra).multiply(muc).divide(tong, 0, RoundingMode.HALF_UP)
                    .subtract(truoc.multiply(muc).divide(tong, 0, RoundingMode.HALF_UP));
        }
        return new Tien(giaTri, giaTri.subtract(don.getConNo()).max(BigDecimal.ZERO));
    }

    private static boolean bangTien(BigDecimal a, BigDecimal b) { return a != null && b != null && a.compareTo(b) == 0; }
    private static void canBuoc(YeuCauDoiTra y, Buoc buoc) {
        if (y.getBuoc() != buoc) throw new IllegalStateException("Thao tác không phù hợp với trạng thái hiện tại.");
    }
    private static String noiDung(String s, String ten) {
        if (s == null || s.isBlank() || s.strip().length() > 500) throw new IllegalStateException("Vui lòng nhập " + ten + " (tối đa 500 ký tự).");
        return s.strip();
    }
}

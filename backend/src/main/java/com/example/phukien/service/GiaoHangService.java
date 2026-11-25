package com.example.phukien.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.example.phukien.model.*;
import com.example.phukien.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GiaoHangService {
    private final DonHangRepository dons;
    private final GiaoHangRepository giaoHangs;
    private final NhatKyGiaoRepository nhatKy;
    private final SanPhamRepository sanPhams;
    private static final ZoneId MUI_GIO = ZoneId.of("Asia/Ho_Chi_Minh");

    public GiaoHangService(DonHangRepository dons, GiaoHangRepository giaoHangs, NhatKyGiaoRepository nhatKy,
                           SanPhamRepository sanPhams) {
        this.dons = dons;
        this.giaoHangs = giaoHangs;
        this.nhatKy = nhatKy;
        this.sanPhams = sanPhams;
    }

    public record HoSo(DonHang don, GiaoHang giao, BuocGiao buoc, List<NhatKyGiao> nhatKy, List<ThaoTacGiao> tacVu) {
        public long getLan() { return giao == null ? 0 : giao.getLanCapNhat(); }
    }

    public Page<GiaoHangRepository.Dong> traCuu(String q, BuocGiao buoc, int trang) {
        return giaoHangs.traCuu(q.strip(), buoc == null ? "" : buoc.name(), PageRequest.of(Math.max(0, trang), 30));
    }

    private static BuocGiao buocHienTai(DonHang don, GiaoHang giao) {
        if (giao != null) return giao.getBuoc();
        return switch (don.getTrangThai()) {
            case DA_DUYET -> BuocGiao.CHO_YEU_CAU;
            case DANG_GIAO -> BuocGiao.DANG_GIAO;
            case HOAN_THANH -> BuocGiao.DA_GIAO;
            default -> null;
        };
    }

    @Transactional(readOnly = true)
    public HoSo xem(int id) {
        DonHang don = dons.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        GiaoHang giao = giaoHangs.findByDon_IdDonHang(id).orElse(null);
        BuocGiao buoc = buocHienTai(don, giao);
        List<ThaoTacGiao> tacVu = Arrays.stream(ThaoTacGiao.values()).filter(t -> t.getTu() == buoc).toList();
        return new HoSo(don, giao, buoc, giao == null ? List.of() : nhatKy.findByGiaoHang_IdOrderByIdDesc(giao.getId()), tacVu);
    }

    public record CapNhat(ThaoTacGiao thaoTac, long lan, String donVi, String vanDon, String ghiChu) {}

    @Transactional
    public void xuLy(int id, CapNhat lenh, String nguoiGhi) {
        capNhat(khoa(id), lenh, nguoiGhi);
    }

    @Transactional
    public void khachNhan(int id, long lan, String taiKhoan) {
        DonHang don = khoa(id);
        if (!don.getKhachHang().getTenDangNhap().equals(taiKhoan)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        capNhat(don, new CapNhat(ThaoTacGiao.XAC_NHAN, lan, "", "", "Khách xác nhận nhận hàng qua tài khoản " + taiKhoan), taiKhoan);
    }

    private DonHang khoa(int id) {
        return dons.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void capNhat(DonHang don, CapNhat lenh, String nguoiGhi) {
        GiaoHang giao = giaoHangs.findByDon_IdDonHang(don.getIdDonHang()).orElse(null);
        BuocGiao buoc = buocHienTai(don, giao);
        ThaoTacGiao t = lenh.thaoTac();
        if (lenh.lan() != (giao == null ? 0 : giao.getLanCapNhat()) || t.getTu() != buoc)
            throw new IllegalStateException("Trạng thái giao hàng đã thay đổi hoặc chưa đến bước này. Vui lòng kiểm tra lại.");
        boolean truocBanGiao = buoc == BuocGiao.CHO_YEU_CAU || buoc == BuocGiao.CHO_XUAT || buoc == BuocGiao.CO_PHIEU;
        if (don.getTrangThai() != (truocBanGiao ? TrangThaiDon.DA_DUYET : TrangThaiDon.DANG_GIAO))
            throw new IllegalStateException("Trạng thái đơn hàng không cho phép xử lý vận chuyển.");
        String ghiChu = lenh.ghiChu().strip();
        if (ghiChu.length() > 500 || (t.getNhanGhiChu() != null && ghiChu.isEmpty()))
            throw new IllegalStateException("Vui lòng điền đủ thông tin của thao tác, tối đa 500 ký tự.");
        if (giao == null) giao = new GiaoHang(don, buoc, !truocBanGiao);
        LocalDateTime luc = LocalDateTime.now(MUI_GIO);
        switch (t) {
            case YEU_CAU -> giao.yeuCau(luc);
            case LAP_PHIEU -> {
                String donVi = lenh.donVi().strip();
                String ma = lenh.vanDon().strip().toUpperCase(Locale.ROOT);
                if (donVi.isEmpty() || donVi.length() > 100 || ma.isEmpty() || ma.length() > 80)
                    throw new IllegalStateException("Cần đơn vị hoặc người giao (tối đa 100 ký tự) và mã vận đơn hoặc mã giao nội bộ (tối đa 80 ký tự).");
                giao.lapPhieu(donVi, ma, luc, nguoiGhi);
            }
            case BAN_GIAO, XAC_NHAN -> {
                if (don.getPhuongThuc() != PhuongThucThanhToan.COD && don.getConNo().signum() > 0)
                    throw new IllegalStateException("Đơn trả trước cần được thanh toán đủ trước khi bàn giao hoặc xác nhận đã giao.");
                don.setTrangThai(t == ThaoTacGiao.BAN_GIAO ? TrangThaiDon.DANG_GIAO : TrangThaiDon.HOAN_THANH);
                if (t == ThaoTacGiao.XAC_NHAN) don.chotBanHang(luc);
            }
            case NHAN_HOAN -> {
                // Hàng thường và hàng đặt trước đã được giữ trước khi bàn giao.
                // Chỉ nhập lại khi kho xác nhận nhận đủ hàng có thể bán.
                don.getChiTiets().stream().sorted(Comparator.comparing(ct -> ct.getSanPham().getIdSanPham()))
                        .forEach(ct -> sanPhams.hoanTon(ct.getSanPham().getIdSanPham(), ct.getSoLuong()));
                don.setTrangThai(TrangThaiDon.DA_HUY);
            }
            case CAP_NHAT, BAO_LOI, GIAO_LAI, CHUYEN_HOAN -> { /* Chỉ thay đổi tiến độ vận chuyển. */ }
        }
        giao.chuyen(t.getDen());
        if (giao.getId() == null) giaoHangs.save(giao);
        nhatKy.save(new NhatKyGiao(giao, luc, nguoiGhi, t.getNhan(), ghiChu));
    }

    // DonHangService đã giữ khóa đơn và chịu trách nhiệm hoàn phần tồn kho đã giữ.
    void huyTruocKhiGiao(DonHang don, String nguoiGhi) {
        giaoHangs.findByDon_IdDonHang(don.getIdDonHang()).ifPresent(giao -> {
            if (giao.getBuoc() != BuocGiao.CHO_XUAT && giao.getBuoc() != BuocGiao.CO_PHIEU)
                throw new IllegalStateException("Đơn đã bàn giao. Vui lòng xử lý trong mục Giao hàng.");
            giao.chuyen(BuocGiao.DA_HUY);
            nhatKy.save(new NhatKyGiao(giao, LocalDateTime.now(MUI_GIO), nguoiGhi, "Hủy trước khi bàn giao", ""));
        });
    }
}

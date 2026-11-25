package com.example.phukien.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import com.example.phukien.model.DonHang;
import com.example.phukien.model.PhieuThanhToan;
import com.example.phukien.model.PhieuThanhToan.HinhThuc;
import com.example.phukien.model.PhieuThanhToan.Loai;
import com.example.phukien.model.TrangThaiDon;
import com.example.phukien.repository.DonHangRepository;
import com.example.phukien.repository.PhieuThanhToanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ThanhToanService {
    private final DonHangRepository dons;
    private final PhieuThanhToanRepository phieus;

    public ThanhToanService(DonHangRepository dons, PhieuThanhToanRepository phieus) {
        this.dons = dons;
        this.phieus = phieus;
    }

    public record SoDon(DonHang don, List<PhieuThanhToan> phieus) {}

    @Transactional(readOnly = true)
    public SoDon soDon(int id) {
        DonHang don = dons.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new SoDon(don, phieus.findByDon_IdDonHangOrderByIdDesc(id));
    }

    public PhieuThanhToan phieu(int donId, long id) {
        return phieus.findByIdAndDon_IdDonHang(id, donId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public record Lenh(Loai loai, HinhThuc hinhThuc, BigDecimal soTien, String thamChieu,
                       String nguoiNopNhan, String noiDung) {}

    @Transactional
    public PhieuThanhToan lap(int id, UUID yeuCau, Lenh lenh, String nguoiLap) {
        DonHang don = dons.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ghi(don, "PHIEU:" + yeuCau, lenh, nguoiLap, false);
    }

    @Transactional
    public boolean nhanChuyenKhoan(String maDon, BigDecimal tien, String idSePay, String thamChieu) {
        var tim = dons.findByMaDon(maDon);
        if (tim.isEmpty()) return false;
        DonHang don = tim.get();
        String khoa;
        if (!idSePay.isBlank()) khoa = "SEPAY:" + idSePay.strip();
        else if (!thamChieu.isBlank()) khoa = "BANK:" + thamChieu.strip().toUpperCase(Locale.ROOT);
        else {
            // Webhook cũ chỉ hỗ trợ một lần tất toán, không suy đoán hai khoản nhỏ bằng nhau.
            if (don.getTongTien() == null || tien.compareTo(don.getTongTien()) != 0
                    || don.getTongThu().signum() != 0 || don.getTrangThai() == TrangThaiDon.DA_HUY) {
                if (phieus.findByYeuCau("SEPAY-CU:" + maDon).filter(p -> p.getSoTien().compareTo(tien) == 0).isPresent()) return true;
                throw new IllegalStateException("Cần ID giao dịch hoặc mã tham chiếu ngân hàng để đối chiếu.");
            }
            khoa = "SEPAY-CU:" + maDon;
        }
        if (khoa.length() > 120) throw new IllegalStateException("Mã giao dịch quá dài.");
        String ref = thamChieu.isBlank() ? idSePay : thamChieu;
        ghi(don, khoa, new Lenh(Loai.THU, HinhThuc.CHUYEN_KHOAN, tien, ref,
                "Theo giao dịch ngân hàng", "Chuyển khoản theo mã đơn " + maDon), "SePay", true);
        return true;
    }

    private PhieuThanhToan ghi(DonHang don, String khoa, Lenh lenh, String nguoiLap, boolean tuNganHang) {
        BigDecimal tien = lenh.soTien();
        if (tien == null || tien.signum() <= 0 || tien.compareTo(BigDecimal.TEN.pow(15)) >= 0
                || tien.stripTrailingZeros().scale() > 0) throw new IllegalStateException("Số tiền phải là số nguyên dương, tối đa 15 chữ số.");
        if (lenh.hinhThuc() == HinhThuc.SO_DU_DAU) throw new IllegalStateException("Vui lòng chọn tiền mặt hoặc chuyển khoản.");
        String nguoi = lenh.nguoiNopNhan().strip();
        String lyDo = lenh.noiDung().strip();
        if (nguoi.isEmpty() || nguoi.length() > 255 || lyDo.length() > 255 || (lenh.loai() == Loai.HOAN && lyDo.isEmpty()))
            throw new IllegalStateException("Điền người nộp/nhận và lý do hoàn tiền. Mỗi mục tối đa 255 ký tự.");
        String ref = lenh.thamChieu().strip().toUpperCase(Locale.ROOT);
        if (ref.length() > 100 || (lenh.hinhThuc() == HinhThuc.CHUYEN_KHOAN && !tuNganHang && ref.isEmpty()))
            throw new IllegalStateException("Cần mã tham chiếu ngân hàng, tối đa 100 ký tự.");
        String doiSoat = lenh.hinhThuc() == HinhThuc.CHUYEN_KHOAN && !ref.isEmpty() ? lenh.loai() + ":" + ref : null;

        var cu = phieus.findByYeuCau(khoa);
        if (cu.isEmpty() && doiSoat != null) cu = phieus.findByDoiSoat(doiSoat);
        if (cu.isPresent()) {
            var p = cu.get();
            if (!don.getIdDonHang().equals(p.getDon().getIdDonHang()) || p.getLoai() != lenh.loai()
                    || p.getHinhThuc() != lenh.hinhThuc() || p.getSoTien().compareTo(tien) != 0
                    || !Objects.equals(doiSoat, p.getDoiSoat())
                    || (!tuNganHang && khoa.equals(p.getYeuCau())
                        && (!nguoi.equals(p.getNguoiNopNhan()) || !lyDo.equals(p.getNoiDung())))) {
                throw new IllegalStateException("Mã phiếu hoặc mã ngân hàng đã được dùng cho một giao dịch khác.");
            }
            return p;
        }

        if (lenh.loai() == Loai.THU && !tuNganHang
                && (don.getTrangThai() == TrangThaiDon.DA_HUY || tien.compareTo(don.getConNo()) > 0))
            throw new IllegalStateException("Số thu vượt số còn phải trả hoặc đơn đã hủy.");
        BigDecimal giam = BigDecimal.ZERO;
        if (lenh.loai() == Loai.HOAN) {
            if (tien.compareTo(don.getGioiHanHoan()) > 0)
                throw new IllegalStateException("Số tiền vượt mức được hoàn của đơn.");
            if (don.getTrangThai() == TrangThaiDon.HOAN_THANH) giam = tien.subtract(don.getCanHoan()).max(BigDecimal.ZERO);
        }
        chuyenSoDuCu(don);
        var phieu = new PhieuThanhToan(don, lenh.loai(), lenh.hinhThuc(), tien, giam, khoa, doiSoat, nguoiLap, nguoi, lyDo);
        don.ghiNhanPhieu(lenh.loai(), tien, giam);
        return phieus.saveAndFlush(phieu);
    }

    // Hồ sơ đổi trả đã khóa đơn. Ghi giảm giá trị hàng trả cả khi khách còn nợ.
    PhieuThanhToan hoanHang(DonHang don, long hoSo, BigDecimal giaTri, HinhThuc hinhThuc,
                            String thamChieu, String nguoiNhan, String nguoiLap) {
        chuyenSoDuCu(don);
        BigDecimal tienTra = giaTri.subtract(don.getConNo()).max(BigDecimal.ZERO);
        don.ghiGiamHangTra(giaTri);
        if (tienTra.signum() == 0) return null;
        if (hinhThuc == null) throw new IllegalStateException("Chọn hình thức hoàn tiền.");
        String khoa = "DOITRA:" + hoSo;
        var phieu = ghi(don, khoa, new Lenh(Loai.HOAN, hinhThuc, tienTra, thamChieu, nguoiNhan,
                "Trả hàng theo yêu cầu ĐT-" + hoSo), nguoiLap, false);
        if (!khoa.equals(phieu.getYeuCau())) throw new IllegalStateException("Mã giao dịch đã dùng trên phiếu khác.");
        return phieu;
    }

    private void chuyenSoDuCu(DonHang don) {
        if (don.canChuyenSoDuCu()) {
            if (don.getTongThu().signum() > 0)
                phieus.save(new PhieuThanhToan(don, Loai.THU, HinhThuc.SO_DU_DAU, don.getTongThu(), BigDecimal.ZERO,
                        "CU:" + don.getIdDonHang(), null, "Hệ thống", don.getKhachHang().getTenDangNhap(), "Chuyển số dư từ trạng thái đã thanh toán cũ"));
            don.khoiTaoSoDu();
        }
    }
}

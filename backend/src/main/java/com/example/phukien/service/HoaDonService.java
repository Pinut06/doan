package com.example.phukien.service;

import java.math.BigDecimal;
import java.util.*;
import com.example.phukien.model.*;
import com.example.phukien.model.HoaDon.Loai;
import com.example.phukien.repository.DonHangRepository;
import com.example.phukien.repository.HoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class HoaDonService {
    private final DonHangRepository dons;
    private final HoaDonRepository hoaDons;

    public HoaDonService(DonHangRepository dons, HoaDonRepository hoaDons) {
        this.dons = dons;
        this.hoaDons = hoaDons;
    }
    public record HoSo(DonHang don, List<HoaDon> cacBan) {}
    public record NoiDung(List<DongHoaDon> dongs, BigDecimal giam, BigDecimal phaiTra) {}
    public record ChiTiet(HoaDon hd, NoiDung noiDung) {}
    public record Mau(String nguoiMua, String diaChi, String mst, String lyDo,
                      List<DongHoaDon> dongs, BigDecimal giam) {}

    public Page<HoaDon> traCuu(String q, String loc, int trang) {
        return hoaDons.traCuu(q.strip(), loc, PageRequest.of(Math.max(0, trang), 25));
    }
    public HoSo hoSo(int id, String khach) {
        var don = docDon(id, khach);
        var ds = hoaDons.findByDonIdOrderByLanLapDesc(id);
        return new HoSo(don, khach == null ? ds : ds.stream().filter(h -> !h.isNhap()).toList());
    }
    private DonHang docDon(int id, String khach) {
        var don = dons.findById(id).orElseThrow(HoaDonService::khongThay);
        if (khach != null && !khach.equals(don.getKhachHang().getTenDangNhap())) throw khongThay();
        return don;
    }
    private static ResponseStatusException khongThay() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }
    private HoaDon tim(int donId, long hd) {
        return hoaDons.findByDonIdAndId(donId, hd).orElseThrow(HoaDonService::khongThay);
    }
    private HoaDon docBan(int donId, long hd, String khach) {
        docDon(donId, khach);
        var h = tim(donId, hd);
        if (khach != null && h.isNhap()) throw khongThay();
        return h;
    }
    public ChiTiet xem(int donId, long id, String khach) {
        var h = docBan(donId, id, khach);
        return new ChiTiet(h, noiDung(h, banTruoc(h)));
    }
    public HoaDon taiXml(int donId, long id, String khach) {
        var h = docBan(donId, id, khach);
        if (h.isNhap()) throw khongThay();
        return h;
    }
    private HoaDon banTruoc(HoaDon h) {
        return h.getBanTruocId() == null ? null : tim(h.getDonId(), h.getBanTruocId());
    }

    @Transactional
    public long lap(int id, long tuBan, Loai loai, String nguoi) {
        var don = dons.findByIdForUpdate(id).orElseThrow(HoaDonService::khongThay);
        if (don.getTrangThai() != TrangThaiDon.HOAN_THANH)
            throw new IllegalStateException("Chỉ lập hóa đơn cho đơn đã giao thành công.");
        var truoc = hoaDons.findFirstByDonIdOrderByLanLapDesc(id).orElse(null);
        if (truoc != null && truoc.isNhap() && Objects.equals(truoc.getBanTruocId(), tuBan == 0 ? null : Long.valueOf(tuBan))
                && truoc.getLoai() == loai) return truoc.getId();
        if (truoc == null ? (tuBan != 0 || loai != Loai.BAN_HANG)
                          : (truoc.isNhap() || truoc.getId() != tuBan || loai == Loai.BAN_HANG))
            throw new IllegalStateException("Vui lòng mở phiên bản mới nhất của hóa đơn.");
        var h = new HoaDon(don, truoc, loai, nguoi);
        if (truoc == null) {
            var nd = tuDon(don);
            h.thayDong(nd.dongs(), nd.giam());
        }
        return hoaDons.save(h).getId();
    }

    private NoiDung tuDon(DonHang don) {
        var hang = don.getChiTiets().stream().sorted(Comparator.comparing(ChiTietDonHang::getIdChiTiet))
                .map(c -> dong(c.getTenSanPham(), c.getSoLuong(), c.getDonGia())).toList();
        if (hang.isEmpty() || don.getTongTien() == null) throw new IllegalStateException("Đơn chưa có đủ dữ liệu tiền hàng.");
        var tong = tongHang(hang);
        var phaiTra = tien(don.getGiaTriPhaiThu());
        var giam = tien(tong.subtract(phaiTra));
        return new NoiDung(hang, giam, phaiTra);
    }
    private static BigDecimal tongHang(List<DongHoaDon> dongs) {
        return dongs.stream().map(DongHoaDon::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public static DongHoaDon dong(String ten, Integer sl, BigDecimal gia) {
        if (sl == null || sl <= 0) throw new IllegalStateException("Số lượng phải là số nguyên dương.");
        return new DongHoaDon(chu(ten, 255, true), sl, tien(gia));
    }
    private static BigDecimal tien(BigDecimal so) {
        if (so == null || so.signum() < 0 || so.stripTrailingZeros().scale() > 0 || so.compareTo(BigDecimal.TEN.pow(15)) >= 0)
            throw new IllegalStateException("Kiểm tra lại số tiền VND và các khoản giảm trừ.");
        return so.setScale(0);
    }
    private static String chu(String s, int max, boolean batBuoc) {
        String v = s == null ? "" : s.strip();
        if (v.length() > max || (batBuoc && v.isEmpty()) || v.codePoints().anyMatch(c ->
                (c < 32 && c != 9 && c != 10 && c != 13) || c == 0xFFFE || c == 0xFFFF || (c >= 0xD800 && c <= 0xDFFF)))
            throw new IllegalStateException("Kiểm tra tên, địa chỉ, mã số thuế và nội dung đã nhập.");
        return v;
    }

    @Transactional
    public void luu(int id, long ban, long lan, Mau mau) {
        var don = dons.findByIdForUpdate(id).orElseThrow(HoaDonService::khongThay);
        var h = banNhap(id, ban, lan);
        var truoc = banTruoc(h);
        if (truoc == null) {
            var nd = tuDon(don);
            h.thayDong(nd.dongs(), nd.giam());
        } else {
            if (mau.dongs() == null || mau.dongs().size() > 500)
                throw new IllegalStateException("Vui lòng kiểm tra số dòng hàng.");
            var ds = mau.dongs().stream().map(d -> dong(d.getTen(), d.getSoLuong(), d.getDonGia())).toList();
            var giam = tien(mau.giam());
            if (giam.compareTo(tien(tongHang(ds))) > 0) throw new IllegalStateException("Giảm trừ vượt tiền hàng.");
            h.thayDong(ds, giam);
        }
        h.suaThongTin(chu(mau.nguoiMua(),255,true), chu(mau.diaChi(),500,true),
                chu(mau.mst(),30,false), chu(mau.lyDo(),500,truoc != null));
    }

    @Transactional
    public void phatHanh(int id, long ban, long lan, String nguoi) {
        var don = dons.findByIdForUpdate(id).orElseThrow(HoaDonService::khongThay);
        var h = banNhap(id, ban, lan);
        var truoc = banTruoc(h);
        if (truoc == null) {
            var goc = tuDon(don);
            if (!h.getDongs().equals(goc.dongs()) || h.getGiam().compareTo(goc.giam()) != 0)
                throw new IllegalStateException("Số liệu đơn đã thay đổi. Hãy lưu nháp để cập nhật và kiểm tra trước khi phát hành.");
        } else {
            chu(h.getLyDo(), 500, true);
            if (h.getLoai() == Loai.THAY_THE && h.getDongs().isEmpty())
                throw new IllegalStateException("Hóa đơn thay thế cần có ít nhất một dòng hàng.");
        }
        var nd = noiDung(h, truoc);
        if (truoc != null && chenhLech(h, truoc).isEmpty() && h.getGiam().compareTo(truoc.getGiam()) == 0
                && h.getNguoiMua().equals(truoc.getNguoiMua()) && h.getDiaChi().equals(truoc.getDiaChi())
                && Objects.toString(h.getMst(),"").equals(Objects.toString(truoc.getMst(),"")))
            throw new IllegalStateException("Chưa thay đổi nội dung so với hóa đơn tham chiếu.");
        h.capSo(nguoi);
        h.luuXml(HoaDonXml.tao(h, nd));
        if (h.getLoai() == Loai.THAY_THE) {
            for (var cu : hoaDons.findByDonIdOrderByLanLapDesc(id))
                if (!cu.getId().equals(h.getId()) && cu.getThayBoi() == null) cu.biThayThe(h.getSoHoaDon());
        }
    }
    @Transactional
    public void boNhap(int id, long ban, long lan) {
        dons.findByIdForUpdate(id).orElseThrow(HoaDonService::khongThay);
        hoaDons.delete(banNhap(id, ban, lan));
    }
    private HoaDon banNhap(int id, long ban, long lan) {
        var h = tim(id, ban);
        if (!h.isNhap() || h.getLanSua() != lan) throw new IllegalStateException("Bản này đã thay đổi, hãy mở lại hóa đơn.");
        return h;
    }

    private NoiDung noiDung(HoaDon h, HoaDon truoc) {
        if (h.getLoai() == Loai.DIEU_CHINH)
            return new NoiDung(chenhLech(h, truoc), h.getGiam().subtract(truoc.getGiam()), h.getTong().subtract(truoc.getTong()));
        return new NoiDung(List.copyOf(h.getDongs()), h.getGiam(), h.getTong());
    }
    private record MatHang(String ten, BigDecimal gia) {}
    private List<DongHoaDon> chenhLech(HoaDon h, HoaDon truoc) {
        // Cùng mặt hàng và đơn giá có thể xuất hiện trên nhiều dòng.
        Map<MatHang,Long> sl = new LinkedHashMap<>();
        for (var d : truoc.getDongs()) sl.merge(new MatHang(d.getTen(), d.getDonGia()), -(long)d.getSoLuong(), Long::sum);
        for (var d : h.getDongs()) sl.merge(new MatHang(d.getTen(), d.getDonGia()), (long)d.getSoLuong(), Long::sum);
        List<DongHoaDon> ketQua = new ArrayList<>();
        sl.forEach((k,n) -> {
            if (n > Integer.MAX_VALUE || n < -Integer.MAX_VALUE) throw new IllegalStateException("Số lượng điều chỉnh quá lớn.");
            if (n != 0) ketQua.add(new DongHoaDon(k.ten(), n.intValue(), k.gia()));
        });
        return ketQua;
    }
}

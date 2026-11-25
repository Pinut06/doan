package com.example.phukien.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phukien.model.ChiTietDonHang;
import com.example.phukien.model.DonHang;
import com.example.phukien.model.PhuongThucThanhToan;
import com.example.phukien.model.SanPham;
import com.example.phukien.model.KhachHang;
import com.example.phukien.model.TrangThaiDon;
import com.example.phukien.model.UuDaiDon;
import com.example.phukien.repository.DonHangRepository;
import com.example.phukien.repository.SanPhamRepository;
import com.example.phukien.repository.KhachHangRepository;
import com.example.phukien.repository.GioHangRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final KhachHangRepository khachHangs;
    private final GioHangRepository gioHang;
    private final MaGiamGiaService maGiamGia;
    private final GiaoHangService giaoHang;
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public DonHangService(DonHangRepository donHangRepository, SanPhamRepository sanPhamRepository,
                          KhachHangRepository khachHangs, GioHangRepository gioHang, MaGiamGiaService maGiamGia,
                          GiaoHangService giaoHang) {
        this.giaoHang = giaoHang;
        this.maGiamGia = maGiamGia;
        this.donHangRepository = donHangRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.khachHangs = khachHangs;
        this.gioHang = gioHang;
    }

    /** Thông tin người nhận + phương thức thanh toán nhập ở trang thanh toán. */
    public record ThongTinGiao(String hoTen, String soDienThoai, String diaChi, String ghiChu,
                               PhuongThucThanhToan phuongThuc) {
        public ThongTinGiao {
            hoTen = hoTen.strip();
            soDienThoai = soDienThoai.strip();
            diaChi = diaChi.strip();
            ghiChu = ghiChu == null ? "" : ghiChu.strip();
        }

        void kiemTra() {
            if (hoTen.isEmpty() || hoTen.length() > 100 || diaChi.isEmpty() || diaChi.length() > 500 || ghiChu.length() > 500) {
                throw new IllegalStateException("Vui lòng kiểm tra tên người nhận (1–100 ký tự), địa chỉ (1–500) và ghi chú (tối đa 500).");
            }
            if (!soDienThoai.matches("(0|\\+84)[0-9]{9,10}")) throw new IllegalStateException("Số điện thoại không hợp lệ.");
        }
    }

    /**
     * Tạo đơn từ giỏ hàng. Hàng còn trong kho -> 1 đơn thường (trừ kho),
     * hàng đang hết (cho đặt trước) -> 1 đơn đặt trước riêng (không trừ kho).
     */
    @Transactional
    public List<DonHang> datTuGio(int khachId, ThongTinGiao tt, BigDecimal tongDaXem,
                                   String ma, String doiChieu) {
        KhachHang tk = khoaKhach(khachId);
        Map<Integer, Integer> dongs = new TreeMap<>();
        gioHang.docDeDat(khachId).forEach(d -> dongs.put(d.getSanPhamId(), d.getSoLuong()));
        if (dongs.isEmpty()) throw new IllegalStateException("Giỏ hàng đang trống.");
        List<DonHang> dons = taoCacDon(tk, tt, dongs, tongDaXem, null, ma, doiChieu);
        gioHang.xoaTheoKhachHang(khachId);
        return dons;
    }

    @Transactional
    public List<DonHang> taoTaiQuay(int khachId, ThongTinGiao tt, Map<Integer, Integer> dongs, BigDecimal tongDaXem,
                                    UUID yeuCau, String ma, String doiChieu) {
        return taoTaiQuay(khachId, tt, dongs, tongDaXem, yeuCau, ma, doiChieu, null);
    }

    @Transactional
    public List<DonHang> taoTaiQuay(int khachId, ThongTinGiao tt, Map<Integer, Integer> dongs, BigDecimal tongDaXem,
                                    UUID yeuCau, String ma, String doiChieu, String nguoiBan) {
        KhachHang kh = khoaKhach(khachId);
        List<DonHang> daTao = donHangRepository.findByMaYeuCauInOrderByIdDonHangAsc(List.of(yeuCau + "PG", yeuCau + "PT"));
        if (!daTao.isEmpty()) {
            if (!daTao.get(0).getKhachHang().getIdKhachHang().equals(khachId)) {
                throw new IllegalStateException("Phiếu tạo đơn này đã được sử dụng. Vui lòng mở phiếu mới.");
            }
            return daTao;
        }
        var dons = taoCacDon(kh, tt, dongs, tongDaXem, yeuCau, ma, doiChieu);
        dons.forEach(d -> d.ghiNhanNguoiBan(nguoiBan));
        return dons;
    }

    private KhachHang khoaKhach(int id) {
        return khachHangs.khoa(id).filter(k -> Boolean.TRUE.equals(k.getKichHoat()))
                .orElseThrow(() -> new IllegalStateException("Khách hàng không tồn tại hoặc tài khoản đã khóa."));
    }

    private List<DonHang> taoCacDon(KhachHang tk, ThongTinGiao tt, Map<Integer, Integer> dongs,
                                     BigDecimal tongDaXem, UUID yeuCau, String ma, String doiChieu) {
        tt.kiemTra();
        if (dongs.isEmpty()) throw new IllegalStateException("Vui lòng chọn ít nhất một sản phẩm.");
        List<GioHangService.Dong> thuong = new ArrayList<>();
        List<GioHangService.Dong> datTruoc = new ArrayList<>();
        LocalDate ngay = SanPham.ngayBan();
        BigDecimal tongHienTai = BigDecimal.ZERO;
        for (var d : new TreeMap<>(dongs).entrySet()) {
            // đọc lại sản phẩm (khóa ghi) để kiểm tra tồn kho chính xác
            SanPham sp = sanPhamRepository.findByIdForUpdate(d.getKey())
                    .orElseThrow(() -> new IllegalStateException("Sản phẩm không còn tồn tại."));
            if (!Boolean.TRUE.equals(sp.getTrangThai())) {
                throw new IllegalStateException("Sản phẩm \"" + sp.getTenSanPham() + "\" đã ngừng bán.");
            }
            if (d.getValue() < 1 || d.getValue() > GioHangService.TOI_DA_MOI_SAN_PHAM) {
                throw new IllegalStateException("Số lượng mỗi sản phẩm phải từ 1 đến 10.");
            }
            GioHangService.Dong dong = new GioHangService.Dong(sp, d.getValue(), sp.giaBanNgay(ngay));
            tongHienTai = tongHienTai.add(dong.getThanhTien());
            if (sp.isHetHang()) {
                if (!sp.isDatTruocDuoc()) {
                    throw new IllegalStateException("Sản phẩm \"" + sp.getTenSanPham() + "\" đã hết hàng.");
                }
                datTruoc.add(dong);
            } else {
                if (dong.soLuong() > sp.getSoLuongTon()) {
                    throw new IllegalStateException("Sản phẩm \"" + sp.getTenSanPham() + "\" chỉ còn "
                            + sp.getSoLuongTon() + " cái trong kho.");
                }
                thuong.add(dong);
            }
        }

        if (tongDaXem != null && tongDaXem.compareTo(tongHienTai) != 0) {
            throw new IllegalStateException("Giá bán hoặc số lượng đã thay đổi. Vui lòng kiểm tra tổng tiền và xác nhận lại.");
        }
        BigDecimal tongThuong = thuong.stream().map(GioHangService.Dong::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
        var gia = maGiamGia.tinh(ma, tongThuong, tongHienTai.subtract(tongThuong), ngay, true);
        if (!gia.doiChieu().equals(doiChieu)) {
            throw new IllegalStateException("Vui lòng áp dụng lại mã và kiểm tra số tiền phải trả trước khi tạo đơn.");
        }
        List<DonHang> kq = new ArrayList<>();
        if (!thuong.isEmpty()) {
            kq.add(taoDon(tk, tt, thuong, false, yeuCau, gia.thuong()));
        }
        if (!datTruoc.isEmpty()) {
            kq.add(taoDon(tk, tt, datTruoc, true, yeuCau, gia.datTruoc()));
        }
        return kq;
    }

    private DonHang taoDon(KhachHang tk, ThongTinGiao tt, List<GioHangService.Dong> dongs, boolean datTruoc,
                           UUID yeuCau, UuDaiDon uuDai) {
        DonHang dh = new DonHang();
        if (yeuCau != null) dh.setMaYeuCau(yeuCau + (datTruoc ? "PT" : "PG"));
        dh.setKhachHang(tk);
        dh.setHoTenNhan(tt.hoTen());
        dh.setSoDienThoai(tt.soDienThoai());
        dh.setDiaChi(tt.diaChi());
        dh.setGhiChu(tt.ghiChu());
        dh.setPhuongThuc(tt.phuongThuc());
        dh.setDatTruoc(datTruoc);
        dh.setTrangThai(datTruoc ? TrangThaiDon.CHO_HANG : TrangThaiDon.CHO_XAC_NHAN);

        BigDecimal tong = BigDecimal.ZERO;
        for (GioHangService.Dong d : dongs) {
            SanPham sp = d.sanPham();
            ChiTietDonHang ct = new ChiTietDonHang();
            ct.setSanPham(sp);
            ct.setTenSanPham(sp.getTenSanPham());
            ct.setDonGia(d.donGia());
            ct.setSoLuong(d.soLuong());
            dh.themChiTiet(ct);
            tong = tong.add(ct.getThanhTien());
            if (!datTruoc) {
                sp.setSoLuongTon(sp.getSoLuongTon() - d.soLuong());
                sanPhamRepository.save(sp);
            }
        }
        kiemTraTongTien(tong);
        dh.setUuDai(uuDai);
        dh.tinhTongPhaiTra(tong);
        dh = donHangRepository.save(dh);
        dh.setMaDon((datTruoc ? "PT" : "PG") + String.format("%06d", dh.getIdDonHang()));
        return donHangRepository.save(dh);
    }

    public Optional<DonHang> get(Integer id) {
        return donHangRepository.findById(id);
    }

    public List<DonHang> getCuaKhachHang(Integer khachHangId) {
        return donHangRepository.findByKhachHang_IdKhachHangOrderByIdDonHangDesc(khachHangId);
    }

    public List<DonHang> getAll() {
        return donHangRepository.findAllByOrderByIdDonHangDesc();
    }

    public long count() {
        return donHangRepository.count();
    }

    public List<DonHang> traCuu(Integer khachId, TrangThaiDon tt, String maDon) {
        return donHangRepository.traCuu(khachId, tt, maDon.strip());
    }

    private DonHang khoaDon(Integer id) {
        return donHangRepository.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void giuTon(ChiTietDonHang ct, int soLuong) {
        if (sanPhamRepository.giuTon(ct.getSanPham().getIdSanPham(), soLuong) != 1) {
            throw new IllegalStateException("Chưa đủ hàng đang bán để giữ " + soLuong + " × " + ct.getTenSanPham() + ".");
        }
    }

    private static void kiemTraTongTien(BigDecimal tong) {
        if (tong.compareTo(BigDecimal.TEN.pow(15)) >= 0) throw new IllegalStateException("Tổng tiền vượt giới hạn lưu trữ.");
    }

    @Transactional
    public void capNhat(int id, long phienBan, ThongTinGiao tt, Map<Integer, Integer> soLuong) {
        DonHang dh = khoaDon(id);
        if (!dh.isChoSuaGiaoHang()) throw new IllegalStateException("Đơn đã chốt, không thể cập nhật.");
        if (dh.getPhienBan() != phienBan) throw new IllegalStateException("Đơn đã thay đổi. Vui lòng mở lại để cập nhật.");
        tt.kiemTra();
        if (!soLuong.isEmpty()) {
            if (!dh.isChoSuaSoLuong()) throw new IllegalStateException("Chỉ sửa số lượng khi đơn chưa xác nhận và chưa thanh toán.");
            if (soLuong.size() != dh.getChiTiets().size() || dh.getChiTiets().stream().anyMatch(ct -> !soLuong.containsKey(ct.getIdChiTiet()))) {
                throw new IllegalStateException("Danh sách sản phẩm không khớp với đơn hàng.");
            }
            BigDecimal tong = BigDecimal.ZERO;
            for (ChiTietDonHang ct : dh.getChiTiets().stream().sorted(Comparator.comparing(c -> c.getSanPham().getIdSanPham())).toList()) {
                int moi = soLuong.get(ct.getIdChiTiet());
                if (moi < 0 || moi > GioHangService.TOI_DA_MOI_SAN_PHAM) throw new IllegalStateException("Số lượng phải từ 0 đến 10.");
                int lech = moi - ct.getSoLuong();
                if (!Boolean.TRUE.equals(dh.getDatTruoc())) {
                    if (lech > 0) giuTon(ct, lech);
                    if (lech < 0) sanPhamRepository.hoanTon(ct.getSanPham().getIdSanPham(), -lech);
                }
                ct.setSoLuong(moi);
                tong = tong.add(ct.getThanhTien());
            }
            dh.getChiTiets().removeIf(ct -> ct.getSoLuong() == 0);
            if (dh.getChiTiets().isEmpty()) throw new IllegalStateException("Đơn cần ít nhất một sản phẩm. Bạn có thể hủy đơn.");
            kiemTraTongTien(tong);
            dh.tinhTongPhaiTra(tong);
        }
        dh.setHoTenNhan(tt.hoTen());
        dh.setSoDienThoai(tt.soDienThoai());
        dh.setDiaChi(tt.diaChi());
        dh.setGhiChu(tt.ghiChu());
        entityManager.lock(dh, jakarta.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    }

    public long demTheoTrangThai(TrangThaiDon tt) {
        return donHangRepository.countByTrangThai(tt);
    }

    /** Đổi trạng thái; hủy đơn thường sẽ hoàn lại kho. */
    @Transactional
    public void doiTrangThai(Integer id, TrangThaiDon moi, String nguoiGhi) {
        DonHang dh = khoaDon(id);
        if (dh.getTrangThai() == moi) {
            return;
        }
        if (!dh.getTrangThai().buocTiepTheo().contains(moi)) {
            throw new IllegalStateException("Không thể chuyển từ " + dh.getTrangThai().getNhan() + " sang " + moi.getNhan() + ".");
        }
        if (moi == TrangThaiDon.DA_DUYET && Boolean.TRUE.equals(dh.getDatTruoc())) {
            dh.getChiTiets().stream().sorted(Comparator.comparing(ct -> ct.getSanPham().getIdSanPham()))
                    .forEach(ct -> giuTon(ct, ct.getSoLuong()));
        }
        if (moi == TrangThaiDon.DANG_GIAO || moi == TrangThaiDon.HOAN_THANH)
            throw new IllegalStateException("Vui lòng bàn giao và xác nhận nhận hàng theo phiếu giao hàng.");
        if (moi == TrangThaiDon.DA_HUY) {
            giaoHang.huyTruocKhiGiao(dh, nguoiGhi);
            hoanKho(dh);
        }
        dh.setTrangThai(moi);
        if (moi == TrangThaiDon.DA_XAC_NHAN) dh.ghiNhanNguoiBan(nguoiGhi);
        // Xác nhận giao hàng độc lập với phiếu thu COD.
        donHangRepository.save(dh);
    }

    /** Khách tự hủy khi đơn chưa được xác nhận. */
    @Transactional
    public void khachHuy(Integer id) {
        DonHang dh = khoaDon(id);
        if (!dh.choPhepHuy()) {
            throw new IllegalStateException("Đơn hàng đã được xử lý, không thể hủy.");
        }
        hoanKho(dh);
        dh.setTrangThai(TrangThaiDon.DA_HUY);
        donHangRepository.save(dh);
    }



    private void hoanKho(DonHang dh) {
        if (Boolean.TRUE.equals(dh.getDatTruoc()) && dh.getTrangThai() != TrangThaiDon.DA_DUYET) {
            return;
        }
        for (ChiTietDonHang ct : dh.getChiTiets()) {
            sanPhamRepository.hoanTon(ct.getSanPham().getIdSanPham(), ct.getSoLuong());
        }
    }

    @Transactional
    public boolean choMoPhongChuyenKhoan(String maDon, String tenKhach) {
        return donHangRepository.findByMaDon(maDon)
                .filter(d -> d.getKhachHang().getTenDangNhap().equals(tenKhach))
                .filter(d -> d.getPhuongThuc() == PhuongThucThanhToan.CHUYEN_KHOAN && d.getConNo().signum() > 0)
                .isPresent();
    }
}

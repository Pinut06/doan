package com.example.phukien.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.model.DonHang;
import com.example.phukien.model.PhuongThucThanhToan;
import com.example.phukien.model.KhachHang;
import com.example.phukien.model.TrangThaiDon;
import com.example.phukien.config.SepayConfig;
import com.example.phukien.service.DonHangService;
import com.example.phukien.service.GiaoHangService;
import com.example.phukien.model.BuocGiao;
import com.example.phukien.service.MaGiamGiaService;
import com.example.phukien.service.GioHangService;
import com.example.phukien.service.KhachHangService;

@Controller
public class DonHangController {

    private final GioHangService gioHang;
    private final DonHangService donHangService;
    private final KhachHangService khachHangService;
    private final SepayConfig sepayConfig;
    private final MaGiamGiaService maGiamGia;
    private final GiaoHangService giaoHang;

    public DonHangController(GioHangService gioHang, DonHangService donHangService,
                             KhachHangService khachHangService, SepayConfig sepayConfig, MaGiamGiaService maGiamGia,
                             GiaoHangService giaoHang) {
        this.giaoHang = giaoHang;
        this.maGiamGia = maGiamGia;
        this.gioHang = gioHang;
        this.donHangService = donHangService;
        this.khachHangService = khachHangService;
        this.sepayConfig = sepayConfig;
    }

    private KhachHang nguoiDung(Principal p) {
        return khachHangService.getByTenDangNhap(p.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private DonHang donCuaToi(Integer id, Principal p) {
        DonHang dh = donHangService.get(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!dh.getKhachHang().getTenDangNhap().equals(p.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return dh;
    }


    @GetMapping("/thanh-toan")
    public String formThanhToan(Principal principal, Model model, RedirectAttributes ra) {
        KhachHang tk = nguoiDung(principal);
        List<GioHangService.Dong> dongs = gioHang.getDongs(tk);
        if (dongs.isEmpty()) {
            ra.addFlashAttribute("loi", "Giỏ hàng đang trống.");
            return "redirect:/gio-hang";
        }
        if (!model.containsAttribute("hoTen")) {
            model.addAttribute("hoTen", tk.getHoTen());
        }
        guiDuLieuForm(model, dongs);
        return "thanh-toan";
    }

    @PostMapping("/thanh-toan")
    public String datHang(@RequestParam String hoTen,
                          @RequestParam(required = false) java.math.BigDecimal tongDaXem,
                          @RequestParam(defaultValue = "") String ma,
                          @RequestParam(defaultValue = "") String doiChieu,
                          @RequestParam(defaultValue = "false") boolean xemMa,
                          @RequestParam String soDienThoai,
                          @RequestParam String diaChi,
                          @RequestParam(required = false) String ghiChu,
                          @RequestParam PhuongThucThanhToan phuongThuc,
                          Principal principal, Model model, RedirectAttributes ra) {
        KhachHang kh = nguoiDung(principal);
        List<GioHangService.Dong> dongs = gioHang.getDongs(kh);
        if (dongs.isEmpty()) {
            ra.addFlashAttribute("loi", "Giỏ hàng đang trống.");
            return "redirect:/gio-hang";
        }
        String loi = null;
        if (hoTen.isBlank()) {
            loi = "Vui lòng nhập họ tên người nhận.";
        } else if (!soDienThoai.trim().matches("^(0|\\+84)[0-9]{9,10}$")) {
            loi = "Số điện thoại không hợp lệ (ví dụ 0906349828).";
        } else if (diaChi.isBlank()) {
            loi = "Vui lòng nhập địa chỉ nhận hàng.";
        }
        if (!xemMa && loi == null) {
            try {
                List<DonHang> dons = donHangService.datTuGio(kh.getIdKhachHang(),
                        new DonHangService.ThongTinGiao(hoTen.trim(), soDienThoai.trim(), diaChi.trim(),
                                ghiChu == null ? null : ghiChu.trim(), phuongThuc),
                        tongDaXem, ma, doiChieu);
                if (dons.size() == 1) {
                    ra.addFlashAttribute("thongBao", "Đặt hàng thành công! Mã đơn: " + dons.get(0).getMaDon());
                    return "redirect:/don-hang/" + dons.get(0).getIdDonHang();
                }
                ra.addFlashAttribute("thongBao", "Đặt hàng thành công! Đơn có hàng đặt trước nên được tách thành "
                        + dons.size() + " đơn.");
                return "redirect:/don-hang";
            } catch (IllegalStateException e) {
                loi = e.getMessage();
            }
        }
        if (!xemMa && loi != null) model.addAttribute("loi", loi);
        var hienTai = xemMa ? dongs : gioHang.getDongs(kh);
        try {
            model.addAttribute("baoGia", maGiamGia.xem(ma, hienTai));
        } catch (IllegalStateException e) {
            model.addAttribute("loi", e.getMessage());
        }
        model.addAttribute("ma", ma);

        model.addAttribute("hoTen", hoTen);
        model.addAttribute("soDienThoai", soDienThoai);
        model.addAttribute("diaChi", diaChi);
        model.addAttribute("ghiChu", ghiChu);
        model.addAttribute("phuongThucChon", phuongThuc);
        guiDuLieuForm(model, hienTai);
        return "thanh-toan";
    }

    private void guiDuLieuForm(Model model, List<GioHangService.Dong> dongs) {
        model.addAttribute("dongs", dongs);
        model.addAttribute("tongTien", gioHang.tongTien(dongs));
        model.addAttribute("coDatTruoc", dongs.stream().anyMatch(GioHangService.Dong::isDatTruoc));
        model.addAttribute("phuongThucs", PhuongThucThanhToan.values());
    }


    @GetMapping("/don-hang")
    public String danhSach(Principal principal, @RequestParam(defaultValue = "") String q,
                           @RequestParam(required = false) TrangThaiDon trangThai, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("trangThaiChon", trangThai);
        model.addAttribute("cacTrangThai", TrangThaiDon.values());
        model.addAttribute("donHangs", donHangService.traCuu(nguoiDung(principal).getIdKhachHang(), trangThai, q));
        return "don-hang-list";
    }

    @GetMapping("/don-hang/{id}")
    public String chiTiet(@PathVariable Integer id, Principal principal, Model model) {
        var hoSo = giaoHang.xem(id);
        DonHang dh = hoSo.don();
        if (!dh.getKhachHang().getTenDangNhap().equals(principal.getName())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        model.addAttribute("vanChuyen", hoSo);
        model.addAttribute("choNhanHang", hoSo.buoc() == BuocGiao.DANG_GIAO);
        model.addAttribute("donHang", dh);
        if (dh.getPhuongThuc() == PhuongThucThanhToan.CHUYEN_KHOAN) {
            String qrUrl = sepayConfig.buildQrUrl(dh.getConNo().longValueExact(), dh.getMaDon());
            model.addAttribute("qrUrl", qrUrl);
            model.addAttribute("sepayConfig", sepayConfig);
        }
        return "don-hang-detail";
    }

    @GetMapping("/don-hang/{id}/thanh-cong")
    public String thanhToanThanhCong(@PathVariable Integer id, Principal principal, Model model) {
        DonHang dh = donCuaToi(id, principal);
        if (dh.getTrangThai() == TrangThaiDon.DA_HUY || dh.getConNo().signum() != 0)
            return "redirect:/don-hang/" + id;
        model.addAttribute("donHang", dh);
        return "thanh-toan-hoan-tat";
    }

    @GetMapping("/api/don-hang/{id}/trang-thai-thanh-toan")
    @ResponseBody
    public java.util.Map<String, Object> kiemTraTrangThaiThanhToan(@PathVariable Integer id, Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        DonHang dh = donCuaToi(id, principal);
        return java.util.Map.of("daThanhToan", dh.getTrangThai() != TrangThaiDon.DA_HUY && dh.getConNo().signum() == 0,
                "conNo", dh.getConNo(), "phienBan", dh.getPhienBan());
    }

    @PostMapping("/don-hang/{id}/huy")
    public String huy(@PathVariable Integer id, Principal principal, RedirectAttributes ra) {
        DonHang dh = donCuaToi(id, principal);
        try {
            donHangService.khachHuy(dh.getIdDonHang());
            ra.addFlashAttribute("thongBao", "Đã hủy đơn " + dh.getMaDon() + ".");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/don-hang/" + id;
    }

    @PostMapping("/don-hang/{id}/thanh-toan-online")
    public String thanhToanOnline(@PathVariable Integer id, Principal principal, RedirectAttributes ra) {
        DonHang dh = donCuaToi(id, principal);
        if (dh.getPhuongThuc() != PhuongThucThanhToan.ONLINE || Boolean.TRUE.equals(dh.getDaThanhToan())
                || dh.getTrangThai() == com.example.phukien.model.TrangThaiDon.DA_HUY) {
            ra.addFlashAttribute("loi", "Đơn hàng này không thể thanh toán online.");
        } else {
            ra.addFlashAttribute("thongBao", "Đã mô phỏng thanh toán online. Công nợ được cập nhật khi cửa hàng ghi nhận tiền thực nhận.");
        }
        return "redirect:/don-hang/" + id;
    }

    @PostMapping("/don-hang/{id}/da-nhan")
    public String daNhan(@PathVariable int id, @RequestParam(defaultValue = "0") long lan, Principal principal, RedirectAttributes ra) {
        try {
            giaoHang.khachNhan(id, lan, principal.getName());
            ra.addFlashAttribute("thongBao", "Đã ghi nhận khách nhận hàng.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/don-hang/" + id;
    }
}

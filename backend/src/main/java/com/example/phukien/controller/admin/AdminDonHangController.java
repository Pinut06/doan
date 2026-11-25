package com.example.phukien.controller.admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.model.TrangThaiDon;
import com.example.phukien.model.DonHang;
import com.example.phukien.model.PhuongThucThanhToan;
import com.example.phukien.service.SanPhamService;
import com.example.phukien.service.GioHangService;
import com.example.phukien.service.MaGiamGiaService;
import com.example.phukien.service.KhachHangService;
import com.example.phukien.service.DonHangService;

@Controller
@RequestMapping("/admin/don-hang")
public class AdminDonHangController {

    private final DonHangService donHangService;
    private final SanPhamService sanPhams;
    private final KhachHangService khachHangs;
    private final MaGiamGiaService maGiamGia;

    public AdminDonHangController(DonHangService donHangService, SanPhamService sanPhams, KhachHangService khachHangs,
                                 MaGiamGiaService maGiamGia) {
        this.maGiamGia = maGiamGia;
        this.donHangService = donHangService;
        this.sanPhams = sanPhams;
        this.khachHangs = khachHangs;
    }

    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String q,
                           @RequestParam(required = false) TrangThaiDon trangThai, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("trangThaiChon", trangThai);
        model.addAttribute("cacTrangThai", TrangThaiDon.values());
        model.addAttribute("donHangs", donHangService.traCuu(null, trangThai, q));
        return "admin/don-hang-list";
    }

    @GetMapping("/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        DonHang don = tim(id);
        model.addAttribute("donHang", don);
        model.addAttribute("trangThais", don.getTrangThai().buocTiepTheo());
        return "admin/don-hang-detail";
    }

    @PostMapping("/{id}/trang-thai")
    public String doiTrangThai(@PathVariable Integer id, @RequestParam TrangThaiDon trangThai,
                               java.security.Principal principal, RedirectAttributes ra) {
        if (trangThai == TrangThaiDon.DANG_GIAO || trangThai == TrangThaiDon.HOAN_THANH)
            return "redirect:/admin/don-hang/" + id + "/giao-hang";
        try {
            donHangService.doiTrangThai(id, trangThai, principal.getName());
            ra.addFlashAttribute("thongBao", "Đã cập nhật trạng thái: " + trangThai.getNhan() + ".");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/admin/don-hang/" + id;
    }

    @PostMapping("/{id}/da-thanh-toan")
    public String daThanhToan(@PathVariable Integer id) {
        return "redirect:/admin/don-hang/" + id + "/thanh-toan";
    }

    private DonHang tim(int id) {
        return donHangService.get(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/new")
    public String formTao(Model model) {
        model.addAttribute("yeuCau", UUID.randomUUID());
        model.addAttribute("chon", Map.of());
        return formTaoDon(model);
    }

    private String formTaoDon(Model model) {
        model.addAttribute("khachs", khachHangs.getAll().stream().filter(k -> Boolean.TRUE.equals(k.getKichHoat())).toList());
        model.addAttribute("sanPhams", sanPhams.timKiem(null, ""));
        model.addAttribute("phuongThucs", PhuongThucThanhToan.values());
        return "admin/don-hang-form";
    }

    private Map<Integer, Integer> ghepSoLuong(List<Integer> ids, List<Integer> soLuong) {
        Map<Integer, Integer> chon = new LinkedHashMap<>();
        if (ids.size() != soLuong.size()) throw new IllegalStateException("Danh sách sản phẩm và số lượng không khớp.");
        for (int i = 0; i < ids.size(); i++) {
            Integer sl = soLuong.get(i);
            if (ids.get(i) == null || sl == null || chon.put(ids.get(i), sl) != null) {
                throw new IllegalStateException("Vui lòng kiểm tra lại từng dòng sản phẩm.");
            }
        }
        return chon;
    }

    @PostMapping("/new")
    public String tao(@RequestParam(required = false) Integer khachId, @RequestParam UUID yeuCau,
                      @RequestParam(defaultValue = "") String ma, @RequestParam(defaultValue = "") String doiChieu,
                      @RequestParam(defaultValue = "false") boolean xemMa,
                      @RequestParam(defaultValue = "") List<Integer> sanPhamId, @RequestParam(defaultValue = "") List<Integer> soLuong,
                      @RequestParam(defaultValue = "") List<BigDecimal> giaXem,
                      @RequestParam String hoTen, @RequestParam String soDienThoai, @RequestParam String diaChi,
                      @RequestParam(defaultValue = "") String ghiChu, @RequestParam PhuongThucThanhToan phuongThuc,
                      java.security.Principal principal, Model model, RedirectAttributes ra) {
        var giao = new DonHangService.ThongTinGiao(hoTen, soDienThoai, diaChi, ghiChu, phuongThuc);
        model.addAttribute("ma", ma);
        model.addAttribute("khachId", khachId);
        model.addAttribute("yeuCau", yeuCau);
        model.addAttribute("giao", giao);
        model.addAttribute("chon", Map.of());
        try {
            Map<Integer, Integer> chon = ghepSoLuong(sanPhamId, soLuong);
            if (giaXem.size() != soLuong.size()) throw new IllegalStateException("Vui lòng tải lại bảng giá.");
            BigDecimal tongDaXem = BigDecimal.ZERO;
            for (int i = 0; i < giaXem.size(); i++) {
                if (giaXem.get(i) == null) throw new IllegalStateException("Vui lòng kiểm tra đơn giá.");
                tongDaXem = tongDaXem.add(giaXem.get(i).multiply(BigDecimal.valueOf(soLuong.get(i))));
            }
            chon.values().removeIf(sl -> sl == 0);
            model.addAttribute("chon", chon);
            if (xemMa) {
                var dongs = sanPhams.timKiem(null, "").stream()
                        .filter(sp -> chon.containsKey(sp.getIdSanPham()))
                        .map(sp -> new GioHangService.Dong(sp, chon.get(sp.getIdSanPham()))).toList();
                if (dongs.size() != chon.size() || dongs.stream().anyMatch(d -> d.soLuong() < 1
                        || d.soLuong() > GioHangService.TOI_DA_MOI_SAN_PHAM)) {
                    throw new IllegalStateException("Vui lòng kiểm tra sản phẩm và số lượng đã chọn.");
                }
                model.addAttribute("baoGia", maGiamGia.xem(ma, dongs));
                return formTaoDon(model);
            }
            if (khachId == null) throw new IllegalStateException("Vui lòng chọn khách hàng.");
            List<DonHang> dons = donHangService.taoTaiQuay(khachId, giao, chon, tongDaXem, yeuCau, ma, doiChieu, principal.getName());
            ra.addFlashAttribute("thongBao", "Đã tạo đơn " + String.join(", ", dons.stream().map(DonHang::getMaDon).toList()) + ".");
            return dons.size() == 1 ? "redirect:/admin/don-hang/" + dons.get(0).getIdDonHang() : "redirect:/admin/don-hang";
        } catch (IllegalStateException e) {
            model.addAttribute("loi", e.getMessage());
            return formTaoDon(model);
        }
    }

    @GetMapping("/{id}/edit")
    public String formSua(@PathVariable int id, Model model, RedirectAttributes ra) {
        DonHang dh = tim(id);
        if (!dh.isChoSuaGiaoHang()) {
            ra.addFlashAttribute("loi", "Đơn đã chốt, không thể cập nhật.");
            return "redirect:/admin/don-hang/" + id;
        }
        model.addAttribute("don", dh);
        model.addAttribute("giao", new DonHangService.ThongTinGiao(dh.getHoTenNhan(), dh.getSoDienThoai(), dh.getDiaChi(), dh.getGhiChu(), dh.getPhuongThuc()));
        model.addAttribute("chon", Map.of());
        return "admin/don-hang-form";
    }

    @PostMapping("/{id}/edit")
    public String sua(@PathVariable int id, @RequestParam long phienBan,
                      @RequestParam(defaultValue = "") List<Integer> chiTietId, @RequestParam(defaultValue = "") List<Integer> soLuong,
                      @RequestParam String hoTen, @RequestParam String soDienThoai, @RequestParam String diaChi,
                      @RequestParam(defaultValue = "") String ghiChu, Model model, RedirectAttributes ra) {
        var giao = new DonHangService.ThongTinGiao(hoTen, soDienThoai, diaChi, ghiChu, null);
        try {
            Map<Integer, Integer> chon = ghepSoLuong(chiTietId, soLuong);
            model.addAttribute("chon", chon);
            donHangService.capNhat(id, phienBan, giao, chon);
            ra.addFlashAttribute("thongBao", "Đã cập nhật đơn hàng.");
        } catch (IllegalStateException e) {
            DonHang dh = tim(id);
            if (dh.isChoSuaGiaoHang() && dh.getPhienBan() == phienBan) {
                if (!model.containsAttribute("chon")) model.addAttribute("chon", Map.of());
                model.addAttribute("don", dh);
                model.addAttribute("giao", giao);
                model.addAttribute("loi", e.getMessage());
                return "admin/don-hang-form";
            }
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/admin/don-hang/" + id;
    }
}

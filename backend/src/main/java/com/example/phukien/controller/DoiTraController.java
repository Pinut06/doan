package com.example.phukien.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;
import com.example.phukien.model.PhieuThanhToan.HinhThuc;
import com.example.phukien.model.YeuCauDoiTra.Buoc;
import com.example.phukien.model.YeuCauDoiTra.Loai;
import com.example.phukien.service.DoiTraService;
import com.example.phukien.service.DoiTraService.ThaoTac;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DoiTraController {
    private final DoiTraService dichVu;
    public DoiTraController(DoiTraService dichVu) { this.dichVu = dichVu; }

    @GetMapping("/admin/doi-tra")
    public String danhSach(@RequestParam(defaultValue = "") String q, @RequestParam(required = false) Buoc buoc,
                           @RequestParam(defaultValue = "0") int trang, Model model) {
        model.addAttribute("ketQua", dichVu.traCuu(q, buoc, trang));
        model.addAttribute("q", q);
        model.addAttribute("buoc", buoc);
        model.addAttribute("cacBuoc", Buoc.values());
        return "admin/doi-tra-list";
    }

    @GetMapping("/admin/don-hang/{id}/doi-tra")
    public String hoSoAdmin(@PathVariable int id, Model model) { return hoSo(id, null, model); }

    @GetMapping("/don-hang/{id}/doi-tra")
    public String hoSoKhach(@PathVariable int id, Principal p, Model model) { return hoSo(id, p.getName(), model); }

    private String hoSo(int id, String khach, Model model) {
        model.addAttribute("hs", dichVu.xem(id, khach));
        model.addAttribute("cacLoai", Loai.values());
        model.addAttribute("maGui", UUID.randomUUID());
        model.addAttribute("quanTri", khach == null);
        return khach == null ? "admin/doi-tra" : "doi-tra";
    }

    public record MauGui(UUID maGui, Integer dong, Integer soLuong, Loai loai, String lyDo) {}

    @PostMapping("/admin/don-hang/{id}/doi-tra")
    public String tiepNhan(@PathVariable int id, @ModelAttribute MauGui mau, Principal p, RedirectAttributes ra) {
        return gui(id, null, p.getName(), mau, ra);
    }

    @PostMapping("/don-hang/{id}/doi-tra")
    public String yeuCau(@PathVariable int id, @ModelAttribute MauGui mau, Principal p, RedirectAttributes ra) {
        return gui(id, p.getName(), p.getName(), mau, ra);
    }

    private String gui(int id, String khach, String nguoi, MauGui mau, RedirectAttributes ra) {
        try {
            if (mau.maGui() == null || mau.dong() == null || mau.soLuong() == null || mau.loai() == null)
                throw new IllegalStateException("Vui lòng chọn sản phẩm, số lượng và hình thức xử lý.");
            dichVu.tiepNhan(id, khach, nguoi, mau.maGui(), mau.dong(), mau.soLuong(), mau.loai(), mau.lyDo());
            ra.addFlashAttribute("thongBao", "Đã tiếp nhận yêu cầu đổi trả.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        } catch (DataIntegrityViolationException | TransientDataAccessException e) {
            ra.addFlashAttribute("loi", "Hồ sơ vừa thay đổi. Vui lòng đối chiếu lại yêu cầu.");
        }
        return "redirect:" + (khach == null ? "/admin" : "") + "/don-hang/" + id + "/doi-tra";
    }

    @PostMapping("/admin/don-hang/{id}/doi-tra/{yc}")
    public String xuLy(@PathVariable int id, @PathVariable long yc, @RequestParam ThaoTac thaoTac,
                       @RequestParam Buoc buocCu, @RequestParam(defaultValue = "") String ghiChu,
                       @RequestParam(required = false) Integer soBanLai, @RequestParam(required = false) Integer spDoi,
                       @RequestParam(required = false) BigDecimal giaTri, @RequestParam(required = false) BigDecimal tienHoan,
                       @RequestParam(required = false) HinhThuc hinhThuc, @RequestParam(defaultValue = "") String thamChieu,
                       @RequestParam(defaultValue = "") String nguoiNhan, Principal p, RedirectAttributes ra) {
        try {
            dichVu.xuLy(id, yc, new DoiTraService.Lenh(thaoTac, buocCu, ghiChu, soBanLai, spDoi,
                    giaTri, tienHoan, hinhThuc, thamChieu, nguoiNhan), p.getName());
            ra.addFlashAttribute("thongBao", "Đã cập nhật yêu cầu ĐT-" + yc + ".");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        } catch (DataIntegrityViolationException | TransientDataAccessException e) {
            ra.addFlashAttribute("loi", "Chưa ghi nhận xử lý. Vui lòng kiểm tra lại tồn kho và mã giao dịch.");
        }
        return "redirect:/admin/don-hang/" + id + "/doi-tra";
    }
}

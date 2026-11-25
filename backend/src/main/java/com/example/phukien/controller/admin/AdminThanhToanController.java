package com.example.phukien.controller.admin;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;
import com.example.phukien.model.DonHang;
import com.example.phukien.model.PhieuThanhToan.HinhThuc;
import com.example.phukien.model.PhieuThanhToan.Loai;
import com.example.phukien.repository.DonHangRepository;
import com.example.phukien.service.ThanhToanService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminThanhToanController {
    private final ThanhToanService thanhToan;
    private final DonHangRepository dons;
    public AdminThanhToanController(ThanhToanService thanhToan, DonHangRepository dons) {
        this.thanhToan = thanhToan;
        this.dons = dons;
    }

    @GetMapping("/thanh-toan")
    public String congNo(@RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "no") String loc, Model model) {
        var danhSach = dons.traThanhToan(q.strip()).stream().filter(d -> switch (loc) {
            case "no" -> d.getConNo().signum() > 0;
            case "hoan" -> d.getCanHoan().signum() > 0;
            default -> true;
        }).toList();
        model.addAttribute("dons", danhSach);
        model.addAttribute("q", q);
        model.addAttribute("loc", loc);
        model.addAttribute("phaiThu", danhSach.stream().map(DonHang::getConNo).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("phaiHoan", danhSach.stream().map(DonHang::getCanHoan).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "admin/cong-no";
    }

    @GetMapping("/don-hang/{id}/thanh-toan")
    public String so(@PathVariable int id, Model model) {
        var so = thanhToan.soDon(id);
        model.addAttribute("don", so.don());
        model.addAttribute("phieus", so.phieus());
        model.addAttribute("loaiPhieu", Loai.values());
        model.addAttribute("yeuCauThu", UUID.randomUUID());
        model.addAttribute("yeuCauHoan", UUID.randomUUID());
        return "admin/so-thanh-toan";
    }

    @PostMapping("/don-hang/{id}/thanh-toan")
    public String lap(@PathVariable int id, @RequestParam UUID yeuCau, @RequestParam Loai loai,
                      @RequestParam HinhThuc hinhThuc, @RequestParam BigDecimal soTien,
                      @RequestParam(defaultValue = "") String thamChieu, @RequestParam String nguoiNopNhan,
                      @RequestParam(defaultValue = "") String noiDung, Principal principal, RedirectAttributes ra) {
        try {
            var lenh = new ThanhToanService.Lenh(loai, hinhThuc, soTien, thamChieu, nguoiNopNhan, noiDung);
            var p = thanhToan.lap(id, yeuCau, lenh, principal.getName());
            ra.addFlashAttribute("thongBao", "Đã ghi nhận phiếu " + p.getSoPhieu() + ".");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("loi", "Không thể lưu phiếu. Vui lòng đối chiếu lại mã giao dịch và số tiền.");
        }
        return "redirect:/admin/don-hang/" + id + "/thanh-toan";
    }

    @GetMapping("/don-hang/{id}/thanh-toan/{phieu}")
    public String chiTiet(@PathVariable int id, @PathVariable long phieu, Model model) {
        model.addAttribute("p", thanhToan.phieu(id, phieu));
        return "admin/phieu-thanh-toan";
    }
}

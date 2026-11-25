package com.example.phukien.controller.admin;

import java.security.Principal;
import com.example.phukien.model.BuocGiao;
import com.example.phukien.model.ThaoTacGiao;
import com.example.phukien.service.GiaoHangService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminGiaoHangController {
    private final GiaoHangService giaoHang;
    public AdminGiaoHangController(GiaoHangService giaoHang) { this.giaoHang = giaoHang; }

    @GetMapping("/giao-hang")
    public String danhSach(@RequestParam(defaultValue = "") String q, @RequestParam(required = false) BuocGiao buoc,
                           @RequestParam(defaultValue = "0") int trang, Model model) {
        model.addAttribute("ketQua", giaoHang.traCuu(q, buoc, trang));
        model.addAttribute("q", q);
        model.addAttribute("buoc", buoc);
        model.addAttribute("cacBuoc", BuocGiao.values());
        return "admin/giao-hang-list";
    }

    @GetMapping("/don-hang/{id}/giao-hang")
    public String hoSo(@PathVariable int id, Model model) {
        model.addAttribute("hs", giaoHang.xem(id));
        return "admin/giao-hang";
    }

    @GetMapping("/don-hang/{id}/giao-hang/phieu")
    public String phieu(@PathVariable int id, Model model) {
        var hs = giaoHang.xem(id);
        if (hs.giao() == null || hs.giao().getNgayLapPhieu() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        model.addAttribute("hs", hs);
        return "admin/phieu-giao-hang";
    }

    @PostMapping("/don-hang/{id}/giao-hang")
    public String xuLy(@PathVariable int id, @RequestParam ThaoTacGiao thaoTac, @RequestParam long lan,
                       @RequestParam(defaultValue = "") String donVi, @RequestParam(defaultValue = "") String vanDon,
                       @RequestParam(defaultValue = "") String ghiChu, Principal principal, RedirectAttributes ra) {
        try {
            giaoHang.xuLy(id, new GiaoHangService.CapNhat(thaoTac, lan, donVi, vanDon, ghiChu), principal.getName());
            ra.addFlashAttribute("thongBao", "Đã ghi nhận vào lịch sử giao hàng.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("loi", "Không thể lưu phiếu. Vui lòng kiểm tra mã vận đơn đã dùng cho đơn khác.");
        }
        return "redirect:/admin/don-hang/" + id + "/giao-hang";
    }
}

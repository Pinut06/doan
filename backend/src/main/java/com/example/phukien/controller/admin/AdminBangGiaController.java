package com.example.phukien.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import com.example.phukien.model.KhuyenMai;
import com.example.phukien.model.SanPham;
import com.example.phukien.service.BangGiaService;
import com.example.phukien.service.SanPhamService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/san-pham/bang-gia")
public class AdminBangGiaController {
    private final SanPhamService sanPhams;
    private final BangGiaService bangGia;

    public AdminBangGiaController(SanPhamService sanPhams, BangGiaService bangGia) {
        this.sanPhams = sanPhams;
        this.bangGia = bangGia;
    }

    public record LichGiamGia(Integer ma,
            @NotBlank(message = "Vui lòng nhập tên chương trình.")
            @Size(max = 120, message = "Tên chương trình tối đa 120 ký tự.") String ten,
            @NotNull(message = "Vui lòng nhập mức giảm.") @Min(value = 1, message = "Mức giảm từ 1 đến 100%.")
            @Max(value = 100, message = "Mức giảm từ 1 đến 100%.") Integer mucGiam,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull(message = "Vui lòng chọn ngày bắt đầu.") LocalDate tu,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull(message = "Vui lòng chọn ngày kết thúc.") LocalDate den) {}

    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "") String ton, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("ton", ton);
        model.addAttribute("sanPhams", sanPhams.traBangGia(q, ton));
        return "admin/bang-gia";
    }

    private SanPham sanPham(int id) {
        return sanPhams.getSanPham(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public String chiTiet(@PathVariable int id, @RequestParam(required = false) Integer sua, Model model) {
        SanPham sp = sanPham(id);
        KhuyenMai km = sua == null ? null : BangGiaService.timChuongTrinh(sp, sua);
        model.addAttribute("sp", sp);
        model.addAttribute("lich", km == null ? new LichGiamGia(null, null, null, null, null)
                : new LichGiamGia(km.getId(), km.getTen(), km.getPhanTram(), km.getTuNgay(), km.getDenNgay()));
        return "admin/gia-san-pham";
    }

    @PostMapping("/{id}/gia")
    public String doiGia(@PathVariable int id, @RequestParam(required = false) BigDecimal giaCu,
                         @RequestParam BigDecimal giaMoi, RedirectAttributes ra) {
        try {
            bangGia.doiGia(id, giaCu, giaMoi);
            ra.addFlashAttribute("thongBao", "Đã lưu giá niêm yết.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return "redirect:/admin/san-pham/bang-gia/" + id;
    }

    @PostMapping("/{id}/khuyen-mai")
    public String luu(@PathVariable int id, @Valid @ModelAttribute("lich") LichGiamGia lich, BindingResult ketQua,
                      Model model, RedirectAttributes ra) {
        if (!ketQua.hasErrors()) {
            try {
                bangGia.luuKhuyenMai(id, lich.ma(), lich.ten(), lich.mucGiam(), lich.tu(), lich.den());
                ra.addFlashAttribute("thongBao", "Đã lưu chương trình khuyến mãi.");
                return "redirect:/admin/san-pham/bang-gia/" + id;
            } catch (IllegalArgumentException e) {
                model.addAttribute("loi", e.getMessage());
            }
        }
        model.addAttribute("sp", sanPham(id));
        return "admin/gia-san-pham";
    }

    @PostMapping("/{id}/khuyen-mai/{ma}/xoa")
    public String xoa(@PathVariable int id, @PathVariable int ma, RedirectAttributes ra) {
        bangGia.xoaKhuyenMai(id, ma);
        ra.addFlashAttribute("thongBao", "Đã xóa chương trình khuyến mãi.");
        return "redirect:/admin/san-pham/bang-gia/" + id;
    }
}

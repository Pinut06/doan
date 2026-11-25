package com.example.phukien.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.model.DanhMuc;
import com.example.phukien.service.DanhMucService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/danh-muc")
public class AdminDanhMucController {

    private final DanhMucService danhMucService;

    public AdminDanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
        return "admin/danh-muc-list";
    }

    @GetMapping("/new")
    public String formThem(Model model) {
        model.addAttribute("danhMuc", new DanhMuc());
        return "admin/danh-muc-form";
    }

    @GetMapping("/{id}/edit")
    public String formSua(@PathVariable Integer id, Model model) {
        DanhMuc dm = danhMucService.getDanhMuc(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("danhMuc", dm);
        return "admin/danh-muc-form";
    }

    @PostMapping("/save")
    public String luu(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
                      BindingResult ketQua,
                      RedirectAttributes ra) {
        if (ketQua.hasErrors()) {
            return "admin/danh-muc-form";
        }
        danhMucService.save(danhMuc);
        ra.addFlashAttribute("thongBao", "Đã lưu danh mục \"" + danhMuc.getTenDanhMuc() + "\".");
        return "redirect:/admin/danh-muc";
    }

    @PostMapping("/{id}/delete")
    public String xoa(@PathVariable Integer id, RedirectAttributes ra) {
        long soSanPham = danhMucService.demSanPham(id);
        if (soSanPham > 0) {
            ra.addFlashAttribute("loi", "Không thể xóa: danh mục đang có " + soSanPham
                    + " sản phẩm. Hãy chuyển hoặc xóa các sản phẩm đó trước.");
        } else {
            danhMucService.delete(id);
            ra.addFlashAttribute("thongBao", "Đã xóa danh mục.");
        }
        return "redirect:/admin/danh-muc";
    }
}

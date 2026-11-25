package com.example.phukien.controller.admin;

import java.security.Principal;
import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.example.phukien.model.KhachHang;
import com.example.phukien.model.HoSoKhachHang;
import com.example.phukien.model.NhomKhachHang;
import com.example.phukien.service.DonHangService;
import com.example.phukien.service.KhachHangService;

@Controller
@RequestMapping("/admin/tai-khoan")
public class AdminKhachHangController {

    private final KhachHangService khachHangService;
    private final DonHangService donHangService;

    public AdminKhachHangController(KhachHangService khachHangService, DonHangService donHangService) {
        this.khachHangService = khachHangService;
        this.donHangService = donHangService;
    }

    @ModelAttribute("cacNhom")
    public NhomKhachHang[] cacNhom() {
        return NhomKhachHang.values();
    }

    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String q,
                           @RequestParam(required = false) NhomKhachHang nhom, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("nhomChon", nhom);
        model.addAttribute("khachHangs", q.isBlank() && nhom == null
                ? khachHangService.getAll() : khachHangService.traCuu(q, nhom));
        return "admin/tai-khoan-list";
    }

    @GetMapping("/new")
    public String formThem(Model model) {
        model.addAttribute("hoSo", new HoSoKhachHang(null, null, null, null, NhomKhachHang.THUONG));
        return "admin/khach-hang-form";
    }

    @PostMapping("/new")
    public String them(@Valid @ModelAttribute("hoSo") HoSoKhachHang hoSo, BindingResult ketQua,
                       @RequestParam(defaultValue = "") String tenDangNhap, @RequestParam(defaultValue = "") String matKhau,
                       Model model, RedirectAttributes ra) {
        model.addAttribute("tenDangNhap", tenDangNhap);
        if (!ketQua.hasErrors()) {
            try {
                KhachHang kh = khachHangService.themHoSo(tenDangNhap, matKhau, hoSo);
                ra.addFlashAttribute("thongBao", "Đã thêm khách hàng.");
                return "redirect:/admin/tai-khoan/" + kh.getIdKhachHang();
            } catch (IllegalArgumentException e) {
                model.addAttribute("loi", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                model.addAttribute("loi", "Tên đăng nhập hoặc email đã được sử dụng.");
            }
        }
        return "admin/khach-hang-form";
    }

    @GetMapping("/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        model.addAttribute("khach", timKhach(id));
        model.addAttribute("donHangs", donHangService.getCuaKhachHang(id));
        return "admin/khach-hang-detail";
    }

    @GetMapping("/{id}/edit")
    public String formSua(@PathVariable Integer id, Model model) {
        KhachHang kh = timKhach(id);
        model.addAttribute("khach", kh);
        model.addAttribute("hoSo", HoSoKhachHang.tu(kh));
        return "admin/khach-hang-form";
    }

    @PostMapping("/{id}/edit")
    public String sua(@PathVariable Integer id, @Valid @ModelAttribute("hoSo") HoSoKhachHang hoSo,
                      BindingResult ketQua, Model model, RedirectAttributes ra) {
        if (!ketQua.hasErrors()) {
            try {
                khachHangService.suaHoSo(id, hoSo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                ra.addFlashAttribute("thongBao", "Đã cập nhật hồ sơ khách hàng.");
                return "redirect:/admin/tai-khoan/" + id;
            } catch (IllegalArgumentException e) {
                model.addAttribute("loi", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                model.addAttribute("loi", "Email đã được sử dụng.");
            }
        }
        model.addAttribute("khach", timKhach(id));
        return "admin/khach-hang-form";
    }

    private KhachHang timKhach(Integer id) {
        return khachHangService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/toggle")
    public String khoaMo(@PathVariable Integer id, Principal principal, RedirectAttributes ra) {
        KhachHang tk = khachHangService.getById(id).orElse(null);
        if (tk == null) {
            return "redirect:/admin/tai-khoan";
        }
        if (tk.getTenDangNhap().equals(principal.getName())) {
            ra.addFlashAttribute("loi", "Bạn không thể khóa chính tài khoản đang đăng nhập.");
        } else {
            khachHangService.doiTrangThai(id);
            ra.addFlashAttribute("thongBao", "Đã cập nhật trạng thái tài khoản " + tk.getTenDangNhap() + ".");
        }
        return "redirect:/admin/tai-khoan";
    }

    @PostMapping("/{id}/delete")
    public String xoa(@PathVariable Integer id, Principal principal, RedirectAttributes ra) {
        KhachHang tk = khachHangService.getById(id).orElse(null);
        if (tk == null) {
            return "redirect:/admin/tai-khoan";
        }
        if (tk.getTenDangNhap().equals(principal.getName())) {
            ra.addFlashAttribute("loi", "Bạn không thể xóa chính tài khoản đang đăng nhập.");
        } else {
            try {
                khachHangService.delete(id);
                ra.addFlashAttribute("thongBao", "Đã xóa tài khoản " + tk.getTenDangNhap() + ".");
            } catch (DataIntegrityViolationException e) {
                ra.addFlashAttribute("loi", "Không thể xóa: tài khoản đã có đơn hàng. Hãy khóa tài khoản thay vì xóa.");
            }
        }
        return "redirect:/admin/tai-khoan";
    }
}

package com.example.phukien.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.service.KhachHangService;

@Controller
public class AuthController {

    private final KhachHangService khachHangService;

    public AuthController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @GetMapping("/quen-mat-khau")
    public String showForgotPasswordForm() {
        return "quen-mat-khau";
    }

    @PostMapping("/register")
    public String register(@RequestParam String tenDangNhap,
                           @RequestParam String matKhau,
                           @RequestParam String nhapLaiMatKhau,
                           @RequestParam(required = false) String hoTen,
                           @RequestParam(required = false) String email,
                           Model model,
                           RedirectAttributes ra) {
        String ten = tenDangNhap.trim();
        String loi = null;
        if (ten.length() < 3 || ten.length() > 50) {
            loi = "Tên đăng nhập phải từ 3 đến 50 ký tự.";
        } else if (matKhau.length() < 6) {
            loi = "Mật khẩu phải có ít nhất 6 ký tự.";
        } else if (!matKhau.equals(nhapLaiMatKhau)) {
            loi = "Mật khẩu nhập lại không khớp.";
        } else if (khachHangService.tonTai(ten)) {
            loi = "Tên đăng nhập đã tồn tại.";
        }
        else if (!email.isEmpty() && khachHangService.tonTaiEmail(email)) {
            loi = "Email này đã được sử dụng bởi tài khoản khác.";
        }
        if (loi != null) {
            model.addAttribute("loi", loi);
            model.addAttribute("tenDangNhap", ten);
            model.addAttribute("hoTen", hoTen);
            model.addAttribute("email", email);
            return "register";
        }
        khachHangService.taoKhachHang(ten, matKhau, hoTen, email);
        ra.addFlashAttribute("thongBao", "Đăng ký thành công! Hãy đăng nhập.");
        return "redirect:/login";
    }

    @PostMapping("/quen-mat-khau")
    public String processForgotPassword(@RequestParam String tenDangNhap,
                                        @RequestParam String email,
                                        @RequestParam String matKhauMoi,
                                        @RequestParam String nhapLaiMatKhauMoi,
                                        Model model,
                                        RedirectAttributes ra) {
        String ten = tenDangNhap.trim();
        String mail = email.trim();
        String loi = null;

        if (matKhauMoi.length() < 6) {
            loi = "Mật khẩu mới phải có ít nhất 6 ký tự.";
        } else if (!matKhauMoi.equals(nhapLaiMatKhauMoi)) {
            loi = "Mật khẩu nhập lại không khớp.";
        } else if (!khachHangService.xacThucTheoEmail(ten, mail)) { 
            loi = "Tên đăng nhập hoặc Email không chính xác.";
        }

        if (loi != null) {
            model.addAttribute("loi", loi);
            model.addAttribute("tenDangNhap", ten);
            model.addAttribute("email", mail);
            return "auth/forgot-password"; 
        }

        khachHangService.capNhatMatKhauMoi(ten, matKhauMoi);

        ra.addFlashAttribute("thongBao", "Đổi mật khẩu thành công! Hãy đăng nhập lại.");
        return "redirect:/login";
    }
}

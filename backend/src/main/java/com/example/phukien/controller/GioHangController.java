package com.example.phukien.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.model.KhachHang;
import com.example.phukien.service.GioHangService;
import com.example.phukien.service.KhachHangService;

@Controller
@RequestMapping("/gio-hang")
public class GioHangController {

    private static final String LOI_QUAN_TRI =
            "Tài khoản quản trị không dùng để mua hàng. Hãy đăng nhập bằng tài khoản khách hàng.";

    private final GioHangService gioHang;
    private final KhachHangService khachHangService;

    public GioHangController(GioHangService gioHang, KhachHangService khachHangService) {
        this.gioHang = gioHang;
        this.khachHangService = khachHangService;
    }

    private KhachHang khachHang(Principal principal) {
        return khachHangService.getByTenDangNhap(principal.getName()).orElse(null);
    }

    @GetMapping
    public String xem(Principal principal, Model model, RedirectAttributes ra) {
        KhachHang kh = khachHang(principal);
        if (kh == null) {
            ra.addFlashAttribute("loi", LOI_QUAN_TRI);
            return "redirect:/";
        }
        List<GioHangService.Dong> dongs = gioHang.getDongs(kh);
        model.addAttribute("dongs", dongs);
        model.addAttribute("tongTien", gioHang.tongTien(dongs));
        model.addAttribute("coDatTruoc", dongs.stream().anyMatch(GioHangService.Dong::isDatTruoc));
        return "gio-hang";
    }

    @PostMapping("/them")
    public String them(@RequestParam Integer id,
                       @RequestParam(defaultValue = "1") int soLuong,
                       @RequestParam(defaultValue = "false") boolean muaNgay,
                       Principal principal, RedirectAttributes ra) {
        KhachHang kh = khachHang(principal);
        if (kh == null) {
            ra.addFlashAttribute("loi", LOI_QUAN_TRI);
            return "redirect:/san-pham/" + id;
        }
        String loi = gioHang.them(kh, id, soLuong);
        if (loi != null) {
            ra.addFlashAttribute("loi", loi);
        } else {
            ra.addFlashAttribute("thongBao", "Đã thêm vào giỏ hàng.");
        }
        return muaNgay ? "redirect:/thanh-toan" : "redirect:/gio-hang";
    }

    @PostMapping("/cap-nhat")
    public String capNhat(@RequestParam Integer id, @RequestParam int soLuong, Principal principal) {
        KhachHang kh = khachHang(principal);
        if (kh != null) {
            gioHang.capNhat(kh, id, soLuong);
        }
        return "redirect:/gio-hang";
    }

    @PostMapping("/xoa")
    public String xoa(@RequestParam Integer id, Principal principal) {
        KhachHang kh = khachHang(principal);
        if (kh != null) {
            gioHang.xoa(kh, id);
        }
        return "redirect:/gio-hang";
    }
}

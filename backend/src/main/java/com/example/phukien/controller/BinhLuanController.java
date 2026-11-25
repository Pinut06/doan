package com.example.phukien.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.model.BinhLuan;
import com.example.phukien.model.SanPham;
import com.example.phukien.model.KhachHang;
import com.example.phukien.service.BinhLuanService;
import com.example.phukien.service.SanPhamService;
import com.example.phukien.service.KhachHangService;

@Controller
public class BinhLuanController {

    private final BinhLuanService binhLuanService;
    private final SanPhamService sanPhamService;
    private final KhachHangService khachHangService;

    public BinhLuanController(BinhLuanService binhLuanService, SanPhamService sanPhamService,
                              KhachHangService khachHangService) {
        this.binhLuanService = binhLuanService;
        this.sanPhamService = sanPhamService;
        this.khachHangService = khachHangService;
    }

    @PostMapping("/san-pham/{id}/binh-luan")
    public String them(@PathVariable Integer id,
                       @RequestParam String noiDung,
                       @RequestParam(defaultValue = "5") Integer soSao,
                       Principal principal, RedirectAttributes ra) {
        SanPham sp = sanPhamService.getSanPham(id)
                .filter(s -> Boolean.TRUE.equals(s.getTrangThai()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String nd = noiDung == null ? "" : noiDung.trim();
        if (nd.isEmpty()) {
            ra.addFlashAttribute("loiBinhLuan", "Nội dung bình luận không được để trống.");
        } else if (nd.length() > 1000) {
            ra.addFlashAttribute("loiBinhLuan", "Bình luận tối đa 1000 ký tự.");
        } else {
            KhachHang tk = khachHangService.getByTenDangNhap(principal.getName()).orElse(null);
            if (tk == null) {
                ra.addFlashAttribute("loiBinhLuan", "Tài khoản quản trị không dùng để bình luận. Hãy đăng nhập bằng tài khoản khách hàng.");
            } else {
                binhLuanService.them(sp, tk, nd, soSao);
                ra.addFlashAttribute("thongBaoBinhLuan", "Cảm ơn bạn đã đánh giá!");
            }
        }
        return "redirect:/san-pham/" + id + "#binh-luan";
    }

    @PostMapping("/binh-luan/{id}/xoa")
    public String xoa(@PathVariable Integer id, Authentication auth, RedirectAttributes ra) {
        BinhLuan b = binhLuanService.get(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean laAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!laAdmin && !b.getKhachHang().getTenDangNhap().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        binhLuanService.xoa(id);
        ra.addFlashAttribute("thongBaoBinhLuan", "Đã xóa bình luận.");
        return "redirect:/san-pham/" + b.getSanPham().getIdSanPham() + "#binh-luan";
    }
}

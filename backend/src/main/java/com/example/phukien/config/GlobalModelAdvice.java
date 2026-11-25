package com.example.phukien.config;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.phukien.service.GioHangService;
import com.example.phukien.service.KhachHangService;

@ControllerAdvice
public class GlobalModelAdvice {

    private final GioHangService gioHangService;
    private final KhachHangService khachHangService;

    public GlobalModelAdvice(GioHangService gioHangService, KhachHangService khachHangService) {
        this.gioHangService = gioHangService;
        this.khachHangService = khachHangService;
    }

    @ModelAttribute("soLuongGio")
    public int soLuongGio(Principal principal) {
        if (principal == null) {
            return 0;
        }
        return khachHangService.getByTenDangNhap(principal.getName())
                .map(kh -> gioHangService.tongSoLuong(kh.getIdKhachHang()))
                .orElse(0);
    }
}

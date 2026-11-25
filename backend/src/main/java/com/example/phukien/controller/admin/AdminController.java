package com.example.phukien.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.phukien.model.TrangThaiDon;
import com.example.phukien.service.DanhMucService;
import com.example.phukien.service.DonHangService;
import com.example.phukien.service.SanPhamService;
import com.example.phukien.service.KhachHangService;

@Controller
public class AdminController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final KhachHangService khachHangService;
    private final DonHangService donHangService;

    public AdminController(SanPhamService sanPhamService, DanhMucService danhMucService,
                           KhachHangService khachHangService, DonHangService donHangService) {
        this.donHangService = donHangService;
        this.sanPhamService = sanPhamService;
        this.danhMucService = danhMucService;
        this.khachHangService = khachHangService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("tongSanPham", sanPhamService.count());
        model.addAttribute("tongDanhMuc", danhMucService.count());
        model.addAttribute("tongKhachHang", khachHangService.count());
        model.addAttribute("tongDonHang", donHangService.count());
        model.addAttribute("donChoXuLy", donHangService.demTheoTrangThai(TrangThaiDon.CHO_XAC_NHAN)
                + donHangService.demTheoTrangThai(TrangThaiDon.CHO_HANG)
                + donHangService.demTheoTrangThai(TrangThaiDon.DA_XAC_NHAN));
        model.addAttribute("sapHetHang", sanPhamService.getSapHetHang());
        return "admin/dashboard";
    }
}

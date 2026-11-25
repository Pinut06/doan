package com.example.phukien.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.phukien.model.SanPham;
import com.example.phukien.service.BinhLuanService;
import com.example.phukien.service.DanhMucService;
import com.example.phukien.service.SanPhamService;

@Controller
public class HomeController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;

    private final BinhLuanService binhLuanService;

    public HomeController(DanhMucService danhMucService, SanPhamService sanPhamService,
                          BinhLuanService binhLuanService) {
        this.binhLuanService = binhLuanService;
        this.danhMucService = danhMucService;
        this.sanPhamService = sanPhamService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) Integer danhMuc,
                       @RequestParam(required = false) String q,
                       Model model) {
        boolean dangLoc = danhMuc != null || (q != null && !q.isBlank());
        model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
        model.addAttribute("sanPhams", sanPhamService.timKiem(danhMuc, q));
        model.addAttribute("noiBat", dangLoc ? java.util.List.of() : sanPhamService.getNoiBat());
        model.addAttribute("danhMucChon", danhMuc);
        model.addAttribute("q", q);
        model.addAttribute("dangLoc", dangLoc);
        return "home";
    }

    @GetMapping("/san-pham/{id}")
    public String chiTietSanPham(@PathVariable Integer id, Model model) {
        SanPham sanPham = sanPhamService.getSanPham(id)
                .filter(sp -> Boolean.TRUE.equals(sp.getTrangThai()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("lienQuan", sanPhamService.getLienQuan(sanPham));
        model.addAttribute("binhLuans", binhLuanService.getTheoSanPham(id));
        model.addAttribute("diemTb", binhLuanService.diemTrungBinh(id));
        return "product-detail";
    }

}

package com.example.phukien.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.phukien.model.SanPham;
import com.example.phukien.service.SanPhamService;

@RestController
public class SanPhamApiController {
    private final SanPhamService sanPhamService;

    public SanPhamApiController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping(value = "/api/danhsachsanpham", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SanPhamTomTat> danhSachSanPham() {
        return sanPhamService.timKiem(null, null).stream()
                .map(SanPhamTomTat::new)
                .toList();
    }

    public record SanPhamTomTat(Integer idSanPham, String maSanPham, String tenSanPham,
                                String thuongHieu, String danhMuc, BigDecimal giaBan,
                                Integer soLuongTon, String hinhAnh) {
        SanPhamTomTat(SanPham sanPham) {
            this(sanPham.getIdSanPham(), sanPham.getMaSanPham(), sanPham.getTenSanPham(),
                    sanPham.getThuongHieu(),
                    sanPham.getDanhMuc() == null ? null : sanPham.getDanhMuc().getTenDanhMuc(),
                    sanPham.getGiaBan(), sanPham.getSoLuongTon(), sanPham.getHinhAnh());
        }
    }
}

package com.example.phukien.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phukien.model.BinhLuan;
import com.example.phukien.model.SanPham;
import com.example.phukien.model.KhachHang;
import com.example.phukien.repository.BinhLuanRepository;

@Service
public class BinhLuanService {

    private final BinhLuanRepository binhLuanRepository;

    public BinhLuanService(BinhLuanRepository binhLuanRepository) {
        this.binhLuanRepository = binhLuanRepository;
    }

    public List<BinhLuan> getTheoSanPham(Integer sanPhamId) {
        return binhLuanRepository.findBySanPham_IdSanPhamOrderByIdBinhLuanDesc(sanPhamId);
    }

    public Double diemTrungBinh(Integer sanPhamId) {
        return binhLuanRepository.diemTrungBinh(sanPhamId);
    }

    public Optional<BinhLuan> get(Integer id) {
        return binhLuanRepository.findById(id);
    }

    public void them(SanPham sanPham, KhachHang khachHang, String noiDung, Integer soSao) {
        BinhLuan b = new BinhLuan();
        b.setSanPham(sanPham);
        b.setKhachHang(khachHang);
        b.setNoiDung(noiDung.trim());
        b.setSoSao(soSao == null ? 5 : Math.max(1, Math.min(5, soSao)));
        binhLuanRepository.save(b);
    }

    public void xoa(Integer id) {
        binhLuanRepository.deleteById(id);
    }

    @Transactional
    public void xoaTheoSanPham(Integer sanPhamId) {
        binhLuanRepository.xoaTheoSanPham(sanPhamId);
    }

    @Transactional
    public void xoaTheoKhachHang(Integer khachHangId) {
        binhLuanRepository.xoaTheoKhachHang(khachHangId);
    }
}

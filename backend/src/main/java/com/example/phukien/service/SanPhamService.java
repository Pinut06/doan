package com.example.phukien.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phukien.model.SanPham;
import com.example.phukien.repository.GioHangRepository;
import com.example.phukien.repository.SanPhamRepository;

@Service
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    private final BinhLuanService binhLuanService;
    private final GioHangRepository gioHangRepository;

    public SanPhamService(SanPhamRepository sanPhamRepository, BinhLuanService binhLuanService,
                          GioHangRepository gioHangRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.binhLuanService = binhLuanService;
        this.gioHangRepository = gioHangRepository;
    }

    public List<SanPham> getAllSanPham() {
        return sanPhamRepository.findAll();
    }

    public Optional<SanPham> getSanPham(Integer id) {
        return sanPhamRepository.findById(id);
    }

    public List<SanPham> timKiem(Integer danhMucId, String tuKhoa) {
        String q = tuKhoa == null ? "" : tuKhoa.trim();
        return sanPhamRepository.traCuu(danhMucId, q, true, "");
    }

    public List<SanPham> traBangGia(String q, String ton) {
        return sanPhamRepository.traCuu(null, q.strip(), false, ton);
    }

    public List<SanPham> getNoiBat() {
        return sanPhamRepository.findByTrangThaiTrueAndNoiBatTrueOrderByIdSanPhamDesc();
    }

    public List<SanPham> getLienQuan(SanPham sanPham) {
        if (sanPham.getDanhMuc() == null) {
            return List.of();
        }
        return sanPhamRepository.findTop4ByDanhMuc_IdDanhMucAndIdSanPhamNotAndTrangThaiTrue(
                sanPham.getDanhMuc().getIdDanhMuc(), sanPham.getIdSanPham());
    }

    public List<SanPham> getSapHetHang() {
        return sanPhamRepository.findBySoLuongTonLessThanEqualOrderBySoLuongTonAsc(5);
    }

    public SanPham save(SanPham sanPham) {
        return sanPhamRepository.save(sanPham);
    }

    @Transactional
    public void delete(Integer id) {
        binhLuanService.xoaTheoSanPham(id);
        gioHangRepository.xoaTheoSanPham(id);
        sanPhamRepository.deleteById(id);
    }

    public long count() {
        return sanPhamRepository.count();
    }
}

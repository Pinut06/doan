package com.example.phukien.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.phukien.model.DanhMuc;
import com.example.phukien.repository.DanhMucRepository;
import com.example.phukien.repository.SanPhamRepository;

@Service
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;

    public DanhMucService(DanhMucRepository danhMucRepository, SanPhamRepository sanPhamRepository) {
        this.danhMucRepository = danhMucRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAll();
    }

    public Optional<DanhMuc> getDanhMuc(Integer id) {
        return danhMucRepository.findById(id);
    }

    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    public long demSanPham(Integer danhMucId) {
        return sanPhamRepository.countByDanhMuc_IdDanhMuc(danhMucId);
    }

    public void delete(Integer id) {
        danhMucRepository.deleteById(id);
    }

    public long count() {
        return danhMucRepository.count();
    }
}

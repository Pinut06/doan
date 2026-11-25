package com.example.phukien.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.phukien.model.QuanTri;
import com.example.phukien.repository.QuanTriRepository;

@Service
public class QuanTriService {

    private final QuanTriRepository quanTriRepository;
    private final PasswordEncoder passwordEncoder;

    public QuanTriService(QuanTriRepository quanTriRepository, PasswordEncoder passwordEncoder) {
        this.quanTriRepository = quanTriRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<QuanTri> getByTenDangNhap(String tenDangNhap) {
        return quanTriRepository.findByTenDangNhap(tenDangNhap);
    }

    public boolean tonTai(String tenDangNhap) {
        return quanTriRepository.existsByTenDangNhap(tenDangNhap);
    }

    public QuanTri taoQuanTri(String tenDangNhap, String matKhau, String hoTen, String email) {
        QuanTri q = new QuanTri();
        q.setTenDangNhap(tenDangNhap);
        q.setMatKhau(passwordEncoder.encode(matKhau));
        q.setHoTen(hoTen);
        q.setEmail(email);
        q.setKichHoat(true);
        return quanTriRepository.save(q);
    }

    public long count() {
        return quanTriRepository.count();
    }
}

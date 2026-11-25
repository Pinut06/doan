package com.example.phukien.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.phukien.repository.KhachHangRepository;
import com.example.phukien.repository.QuanTriRepository;

@Service
public class DangNhapService implements UserDetailsService {

    private final QuanTriRepository quanTriRepository;
    private final KhachHangRepository khachHangRepository;

    public DangNhapService(QuanTriRepository quanTriRepository, KhachHangRepository khachHangRepository) {
        this.quanTriRepository = quanTriRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var quanTri = quanTriRepository.findByTenDangNhap(username);
        if (quanTri.isPresent()) {
            var q = quanTri.get();
            return User.builder()
                    .username(q.getTenDangNhap())
                    .password(q.getMatKhau())
                    .roles("ADMIN")
                    .disabled(!Boolean.TRUE.equals(q.getKichHoat()))
                    .build();
        }
        var k = khachHangRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));
        return User.builder()
                .username(k.getTenDangNhap())
                .password(k.getMatKhau())
                .roles("USER")
                .disabled(!Boolean.TRUE.equals(k.getKichHoat()))
                .build();
    }
}

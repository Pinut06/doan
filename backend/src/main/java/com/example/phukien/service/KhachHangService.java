package com.example.phukien.service;

import java.util.List;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phukien.model.KhachHang;
import com.example.phukien.model.HoSoKhachHang;
import com.example.phukien.model.NhomKhachHang;
import com.example.phukien.repository.GioHangRepository;
import com.example.phukien.repository.KhachHangRepository;
import com.example.phukien.repository.QuanTriRepository;

@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;
    private final BinhLuanService binhLuanService;
    private final QuanTriRepository quanTriRepository;
    private final GioHangRepository gioHangRepository;

    public KhachHangService(KhachHangRepository khachHangRepository, PasswordEncoder passwordEncoder,
                            BinhLuanService binhLuanService, QuanTriRepository quanTriRepository,
                            GioHangRepository gioHangRepository) {
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
        this.binhLuanService = binhLuanService;
        this.quanTriRepository = quanTriRepository;
        this.gioHangRepository = gioHangRepository;
    }

    public Optional<KhachHang> getByTenDangNhap(String tenDangNhap) {
        return khachHangRepository.findByTenDangNhap(tenDangNhap);
    }

    public List<KhachHang> getAll() {
        return khachHangRepository.findAll();
    }

    public List<KhachHang> traCuu(String tuKhoa, NhomKhachHang nhom) {
        return khachHangRepository.traCuu(tuKhoa.strip(), nhom);
    }

    @Transactional
    public KhachHang themHoSo(String tenDangNhap, String matKhau, HoSoKhachHang hoSo) {
        String ten = tenDangNhap.strip();
        if (ten.length() < 3 || ten.length() > 50) {
            throw new IllegalArgumentException("Tên đăng nhập phải từ 3 đến 50 ký tự.");
        }
        if (matKhau.isBlank() || matKhau.length() < 6 || matKhau.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự và tối đa 72 byte.");
        }
        if (tonTai(ten)) {
            throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng.");
        }
        if (hoSo.email() != null && tonTaiEmail(hoSo.email())) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }
        KhachHang kh = taoKhachHang(ten, matKhau, hoSo.hoTen(), hoSo.email());
        capNhatLienHe(kh, hoSo);
        return kh;
    }

    @Transactional
    public Optional<KhachHang> suaHoSo(Integer id, HoSoKhachHang hoSo) {
        return khachHangRepository.findById(id).map(kh -> {
            if (hoSo.email() != null && khachHangRepository.existsByEmailIgnoreCaseAndIdKhachHangNot(hoSo.email(), id)) {
                throw new IllegalArgumentException("Email đã được sử dụng.");
            }
            kh.setHoTen(hoSo.hoTen());
            kh.setEmail(hoSo.email());
            capNhatLienHe(kh, hoSo);
            return kh;
        });
    }

    private void capNhatLienHe(KhachHang kh, HoSoKhachHang hoSo) {
        kh.setSoDienThoai(hoSo.soDienThoai());
        kh.setDiaChi(hoSo.diaChi());
        kh.setNhomKhachHang(hoSo.nhomKhachHang());
    }

    public Optional<KhachHang> getById(Integer id) {
        return khachHangRepository.findById(id);
    }

    public boolean tonTai(String tenDangNhap) {
        return khachHangRepository.existsByTenDangNhap(tenDangNhap)
                || quanTriRepository.existsByTenDangNhap(tenDangNhap);
    }

    public boolean tonTaiEmail(String email) {
        return khachHangRepository.existsByEmail(email);
    }

    public KhachHang taoKhachHang(String tenDangNhap, String matKhau, String hoTen, String email) {
        KhachHang tk = new KhachHang();
        tk.setTenDangNhap(tenDangNhap);
        tk.setMatKhau(passwordEncoder.encode(matKhau));
        tk.setHoTen(hoTen);
        tk.setEmail(email);
        tk.setKichHoat(true);
        return khachHangRepository.save(tk);
    }

    public void doiTrangThai(Integer id) {
        khachHangRepository.findById(id).ifPresent(tk -> {
            tk.setKichHoat(!Boolean.TRUE.equals(tk.getKichHoat()));
            khachHangRepository.save(tk);
        });
    }

    public boolean xacThucTheoEmail(String tenDangNhap, String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        Optional<KhachHang> khOpt = khachHangRepository.findByTenDangNhap(tenDangNhap);
        
        return khOpt.isPresent() && email.equals(khOpt.get().getEmail());
    }

    public void capNhatMatKhauMoi(String tenDangNhap, String matKhauMoi) {
            Optional<KhachHang> khOpt = khachHangRepository.findByTenDangNhap(tenDangNhap);
            if (khOpt.isPresent()) {
                KhachHang kh = khOpt.get();
                kh.setMatKhau(passwordEncoder.encode(matKhauMoi)); 
                khachHangRepository.save(kh);
            }
        }

    @Transactional
    public void delete(Integer id) {
        binhLuanService.xoaTheoKhachHang(id);
        gioHangRepository.xoaTheoKhachHang(id);
        khachHangRepository.deleteById(id);
    }

    public long count() {
        return khachHangRepository.count();
    }
}

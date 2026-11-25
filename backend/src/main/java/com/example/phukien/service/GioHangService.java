package com.example.phukien.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phukien.model.GioHang;
import com.example.phukien.model.KhachHang;
import com.example.phukien.model.SanPham;
import com.example.phukien.repository.GioHangRepository;
import com.example.phukien.repository.KhachHangRepository;

@Service
public class GioHangService {

    public static final int TOI_DA_MOI_SAN_PHAM = 10;

    public record Dong(SanPham sanPham, int soLuong, BigDecimal donGia) {
        public Dong(SanPham sanPham, int soLuong) {
            this(sanPham, soLuong, sanPham.getGiaBan());
        }
        public boolean isDatTruoc() {
            return sanPham.isHetHang();
        }

        public BigDecimal getThanhTien() {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    private final GioHangRepository gioHangRepository;
    private final KhachHangRepository khachHangs;
    private final SanPhamService sanPhamService;

    public GioHangService(GioHangRepository gioHangRepository, SanPhamService sanPhamService, KhachHangRepository khachHangs) {
        this.gioHangRepository = gioHangRepository;
        this.khachHangs = khachHangs;
        this.sanPhamService = sanPhamService;
    }

    @Transactional
    public String them(KhachHang kh, Integer sanPhamId, int soLuong) {
        khachHangs.khoa(kh.getIdKhachHang()).orElseThrow();
        SanPham sp = sanPhamService.getSanPham(sanPhamId)
                .filter(s -> Boolean.TRUE.equals(s.getTrangThai())).orElse(null);
        if (sp == null) {
            return "Sản phẩm không tồn tại hoặc đã ngừng bán.";
        }
        if (sp.isHetHang() && !sp.isDatTruocDuoc()) {
            return "Sản phẩm \"" + sp.getTenSanPham() + "\" đã hết hàng.";
        }
        GioHang dong = gioHangRepository
                .findByKhachHang_IdKhachHangAndSanPham_IdSanPham(kh.getIdKhachHang(), sanPhamId)
                .orElseGet(() -> {
                    GioHang g = new GioHang();
                    g.setKhachHang(kh);
                    g.setSanPham(sp);
                    g.setSoLuong(0);
                    return g;
                });
        long yeuCau = (long) dong.getSoLuong() + Math.max(1, soLuong);
        int tran = tran(sp);
        String loi = null;
        if (yeuCau > tran) {
            loi = "Chỉ có thể mua tối đa " + tran + " sản phẩm \"" + sp.getTenSanPham() + "\".";
        }
        dong.setSoLuong((int) Math.min(yeuCau, tran));
        gioHangRepository.save(dong);
        return loi;
    }

    @Transactional
    public void capNhat(KhachHang kh, Integer sanPhamId, int soLuong) {
        khachHangs.khoa(kh.getIdKhachHang()).orElseThrow();
        gioHangRepository.findByKhachHang_IdKhachHangAndSanPham_IdSanPham(kh.getIdKhachHang(), sanPhamId)
                .ifPresent(dong -> {
                    int soLuongMoi = Math.min(soLuong, tran(dong.getSanPham()));
                    if (soLuongMoi <= 0) {
                        gioHangRepository.delete(dong);
                    } else {
                        dong.setSoLuong(soLuongMoi);
                        gioHangRepository.save(dong);
                    }
                });
    }

    @Transactional
    public void xoa(KhachHang kh, Integer sanPhamId) {
        khachHangs.khoa(kh.getIdKhachHang()).orElseThrow();
        gioHangRepository.findByKhachHang_IdKhachHangAndSanPham_IdSanPham(kh.getIdKhachHang(), sanPhamId)
                .ifPresent(gioHangRepository::delete);
    }

    @Transactional
    public void lamTrong(KhachHang kh) {
        gioHangRepository.xoaTheoKhachHang(kh.getIdKhachHang());
    }

    @Transactional(readOnly = true)
    public int tongSoLuong(Integer khachHangId) {
        return (int) gioHangRepository.tongSoLuong(khachHangId);
    }

    @Transactional
    public List<Dong> getDongs(KhachHang kh) {
        khachHangs.khoa(kh.getIdKhachHang()).orElseThrow();
        List<Dong> kq = new ArrayList<>();
        for (GioHang g : gioHangRepository.findByKhachHang_IdKhachHangOrderByIdGioHangAsc(kh.getIdKhachHang())) {
            SanPham sp = g.getSanPham();
            if (!Boolean.TRUE.equals(sp.getTrangThai())) {
                gioHangRepository.delete(g);
                continue;
            }
            int sl = g.getSoLuong();
            if (!sp.isHetHang() && sl > sp.getSoLuongTon()) {
                sl = sp.getSoLuongTon();
                g.setSoLuong(sl);
            }
            kq.add(new Dong(sp, sl));
        }
        return kq;
    }

    public BigDecimal tongTien(List<Dong> dongs) {
        return dongs.stream().map(Dong::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int tran(SanPham sp) {
        if (sp.isHetHang()) return sp.isDatTruocDuoc() ? TOI_DA_MOI_SAN_PHAM : 0;
        return Math.min(sp.getSoLuongTon(), TOI_DA_MOI_SAN_PHAM);
    }
}

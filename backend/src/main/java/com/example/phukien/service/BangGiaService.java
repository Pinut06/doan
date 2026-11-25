package com.example.phukien.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.example.phukien.model.KhuyenMai;
import com.example.phukien.model.SanPham;
import com.example.phukien.repository.KhuyenMaiRepository;
import com.example.phukien.repository.SanPhamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BangGiaService {
    private final SanPhamRepository sanPhams;
    private final KhuyenMaiRepository khuyenMais;

    public BangGiaService(SanPhamRepository sanPhams, KhuyenMaiRepository khuyenMais) {
        this.sanPhams = sanPhams;
        this.khuyenMais = khuyenMais;
    }

    private SanPham khoaSanPham(int id) {
        return sanPhams.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void doiGia(int id, BigDecimal giaCu, BigDecimal giaMoi) {
        SanPham sp = khoaSanPham(id);
        if (giaMoi.signum() < 0 || giaMoi.stripTrailingZeros().scale() > 0 || giaMoi.compareTo(BigDecimal.TEN.pow(15)) >= 0) {
            throw new IllegalArgumentException("Giá phải là số tiền nguyên từ 0 đến 999.999.999.999.999 đồng.");
        }
        if (sp.getGiaCoBan() == null ? giaCu != null : giaCu == null || sp.getGiaCoBan().compareTo(giaCu) != 0) {
            throw new IllegalArgumentException("Giá niêm yết đã thay đổi. Vui lòng kiểm tra trước khi lưu lại.");
        }
        sp.setGiaCoBan(giaMoi);
    }

    public static KhuyenMai timChuongTrinh(SanPham sp, int ma) {
        return sp.getLichKhuyenMai().stream().filter(k -> k.getId() == ma).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void luuKhuyenMai(int id, Integer ma, String ten, int mucGiam, LocalDate tu, LocalDate den) {
        SanPham sp = khoaSanPham(id);
        if (den.isBefore(tu)) {
            throw new IllegalArgumentException("Ngày kết thúc phải từ ngày bắt đầu trở đi.");
        }
        boolean trung = sp.getLichKhuyenMai().stream().anyMatch(k -> !Objects.equals(k.getId(), ma)
                && !k.getDenNgay().isBefore(tu) && !k.getTuNgay().isAfter(den));
        if (trung) {
            throw new IllegalArgumentException("Thời gian áp dụng trùng với một chương trình của sản phẩm này.");
        }
        KhuyenMai km = ma == null ? new KhuyenMai(sp) : timChuongTrinh(sp, ma);
        km.capNhat(ten.strip(), mucGiam, tu, den);
        khuyenMais.save(km);
    }

    @Transactional
    public void xoaKhuyenMai(int id, int ma) {
        khuyenMais.delete(timChuongTrinh(khoaSanPham(id), ma));
    }
}

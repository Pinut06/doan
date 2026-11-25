package com.example.phukien.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import com.example.phukien.model.MaGiamGia;
import com.example.phukien.model.SanPham;
import com.example.phukien.model.UuDaiDon;
import com.example.phukien.repository.MaGiamGiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaGiamGiaService {
    private final MaGiamGiaRepository cacMa;

    public MaGiamGiaService(MaGiamGiaRepository cacMa) { this.cacMa = cacMa; }

    public record BaoGia(String doiChieu, UuDaiDon thuong, UuDaiDon datTruoc, BigDecimal tienHang) {
        public BigDecimal getChietKhau() {
            BigDecimal a = thuong == null ? BigDecimal.ZERO : thuong.getTienGiam();
            BigDecimal b = datTruoc == null ? BigDecimal.ZERO : datTruoc.getTienGiam();
            return a.add(b);
        }
        public BigDecimal getPhaiTra() { return tienHang.subtract(getChietKhau()); }
    }

    @Transactional(readOnly = true)
    public BaoGia xem(String ma, List<GioHangService.Dong> dongs) {
        BigDecimal thuong = BigDecimal.ZERO;
        BigDecimal datTruoc = BigDecimal.ZERO;
        for (var dong : dongs) {
            if (dong.isDatTruoc()) datTruoc = datTruoc.add(dong.getThanhTien());
            else thuong = thuong.add(dong.getThanhTien());
        }
        return tinh(ma, thuong, datTruoc, SanPham.ngayBan(), false);
    }

    BaoGia tinh(String ma, BigDecimal tienThuong, BigDecimal tienDatTruoc, LocalDate ngay, boolean khoa) {
        String chuan = MaGiamGia.chuanHoa(ma);
        BigDecimal tong = tienThuong.add(tienDatTruoc);
        if (chuan.isEmpty()) return new BaoGia("", null, null, tong);
        MaGiamGia km = (khoa ? cacMa.khoaTheoMa(chuan) : cacMa.findByMa(chuan))
                .filter(m -> m.coHieuLuc(ngay))
                .orElseThrow(() -> new IllegalStateException("Mã giảm giá không tồn tại hoặc đang ngoài hiệu lực."));
        boolean choThuong = duNguong(tienThuong, km.getNguong());
        boolean choDatTruoc = duNguong(tienDatTruoc, km.getNguong());
        if (!choThuong && !choDatTruoc) throw new IllegalStateException(
                "Chưa có đơn đủ điều kiện. Giá trị tối thiểu sau giảm giá sản phẩm là " + km.getNguong().toPlainString() + " ₫.");
        BigDecimal mucThuong = km.getMuc();
        BigDecimal mucDatTruoc = km.getMuc();
        if (choThuong && choDatTruoc && km.getLoai() == UuDaiDon.Loai.SO_TIEN) {
            // Chia một khoản giảm cho cả lượt mua. Đồng lẻ còn lại thuộc đơn đặt trước.
            BigDecimal nganSach = km.getMuc().min(tong);
            mucThuong = nganSach.multiply(tienThuong).divide(tong, 0, RoundingMode.HALF_UP);
            mucDatTruoc = nganSach.subtract(mucThuong);
        }
        UuDaiDon thuong = choThuong ? new UuDaiDon(chuan, km.getLoai(), mucThuong, km.getNguong(), tienThuong) : null;
        UuDaiDon datTruoc = choDatTruoc ? new UuDaiDon(chuan, km.getLoai(), mucDatTruoc, km.getNguong(), tienDatTruoc) : null;
        String doiChieu = chuan + "/" + km.getPhienBan() + "/" + tienThuong.toPlainString() + "/" + tienDatTruoc.toPlainString();
        return new BaoGia(doiChieu, thuong, datTruoc, tong);
    }

    private static boolean duNguong(BigDecimal tien, BigDecimal nguong) {
        return tien.signum() > 0 && tien.compareTo(nguong) >= 0;
    }
}

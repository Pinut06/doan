package com.example.phukien.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.phukien.model.DanhMuc;
import com.example.phukien.model.SanPham;
import com.example.phukien.repository.DanhMucRepository;
import com.example.phukien.repository.SanPhamRepository;
import com.example.phukien.service.KhachHangService;
import com.example.phukien.service.QuanTriService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KhachHangService khachHangService;
    private final QuanTriService quanTriService;
    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;

    public DataInitializer(KhachHangService khachHangService, QuanTriService quanTriService,
                           DanhMucRepository danhMucRepository, SanPhamRepository sanPhamRepository) {
        this.khachHangService = khachHangService;
        this.quanTriService = quanTriService;
        this.danhMucRepository = danhMucRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    @Override
    public void run(String... args) {
        if (quanTriService.count() == 0 && !khachHangService.tonTai("admin")) {
            quanTriService.taoQuanTri("admin", "admin123", "Quản trị viên", "admin@techgear.vn");
        }
        if (khachHangService.count() == 0 && !khachHangService.tonTai("khach")) {
            khachHangService.taoKhachHang("khach", "khach123", "Khách hàng mẫu", "khach@example.com");
        }
        if (danhMucRepository.count() == 0) {
            DanhMuc taiNghe = dm("Tai nghe", "Tai nghe có dây, không dây, chống ồn");
            DanhMuc sacCap = dm("Sạc & Cáp", "Củ sạc nhanh, cáp USB-C, Lightning");
            DanhMuc chuotPhim = dm("Chuột & Bàn phím", "Chuột, bàn phím cơ và không dây");
            DanhMuc loa = dm("Loa Bluetooth", "Loa mini di động, loa để bàn");
            DanhMuc pin = dm("Pin dự phòng", "Pin sạc dự phòng dung lượng cao");
            DanhMuc opLung = dm("Ốp lưng & Bảo vệ", "Ốp lưng, kính cường lực, túi chống sốc");

            sp("Tai nghe Bluetooth chống ồn ANC Pro", "TN-001", "SoundMax", taiNghe,
                    "Chống ồn chủ động, pin 30 giờ, kết nối Bluetooth 5.3, sạc nhanh 10 phút dùng 5 giờ.",
                    1890000, 25, 12, true, true);
            sp("Tai nghe True Wireless Buds 2", "TN-002", "SoundMax", taiNghe,
                    "Thiết kế nhỏ gọn, âm bass mạnh, chống nước IPX4, hộp sạc 24 giờ.",
                    790000, 40, 12, false, true);
            sp("Tai nghe chụp tai Gaming H7", "TN-003", "GearX", taiNghe,
                    "Âm thanh 7.1, mic khử ồn, đệm tai êm, đèn LED RGB.",
                    650000, 18, 6, false, false);

            sp("Củ sạc nhanh GaN 65W 3 cổng", "SC-001", "Anker", sacCap,
                    "Sạc nhanh laptop, điện thoại, máy tính bảng cùng lúc. Công nghệ GaN nhỏ gọn.",
                    690000, 60, 18, true, true);
            sp("Cáp USB-C to USB-C 100W bọc dù 2m", "SC-002", "Anker", sacCap,
                    "Hỗ trợ sạc 100W, truyền dữ liệu 480Mbps, bọc dù chống đứt gãy.",
                    190000, 120, 12, false, true);

            sp("Chuột không dây Silent Click", "CB-001", "Logi", chuotPhim,
                    "Click êm, kết nối 2.4GHz và Bluetooth, pin dùng 18 tháng.",
                    350000, 50, 12, false, true);
            sp("Bàn phím cơ không dây K87", "CB-002", "Keychron", chuotPhim,
                    "Layout TKL, switch hot-swap, pin 4000mAh, kết nối 3 thiết bị.",
                    1590000, 14, 12, true, false);

            sp("Loa Bluetooth Mini Rock 20W", "LB-001", "JBL", loa,
                    "Công suất 20W, chống nước IP67, pin 12 giờ, ghép đôi 2 loa.",
                    1290000, 22, 12, true, false);
            sp("Loa để bàn Soundbar PC", "LB-002", "Edifier", loa,
                    "Soundbar nhỏ gọn cho góc làm việc, kết nối USB và Bluetooth.",
                    590000, 3, 12, false, false);

            sp("Pin dự phòng 20.000mAh PD 22.5W", "PD-001", "Xiaomi", pin,
                    "Sạc nhanh PD 22.5W, 2 cổng ra, màn hình hiển thị phần trăm pin.",
                    540000, 35, 12, false, true);
            sp("Pin dự phòng MagSafe 10.000mAh", "PD-002", "Anker", pin,
                    "Hít nam châm cho iPhone, sạc không dây 15W, mỏng nhẹ.",
                    1090000, 8, 18, true, false);

            sp("Ốp lưng chống sốc trong suốt", "OL-001", "Spigen", opLung,
                    "Chống ố vàng, viền nâng bảo vệ camera, hỗ trợ sạc không dây.",
                    250000, 90, 6, false, false);
            sp("Kính cường lực chống nhìn trộm", "OL-002", "Spigen", opLung,
                    "Độ cứng 9H, chống vân tay, kèm khung dán dễ căn chỉnh.",
                    120000, 2, 3, false, false);
        }
    }

    private DanhMuc dm(String ten, String moTa) {
        DanhMuc d = new DanhMuc();
        d.setTenDanhMuc(ten);
        d.setMoTa(moTa);
        return danhMucRepository.save(d);
    }

    private void sp(String ten, String ma, String thuongHieu, DanhMuc dm, String moTa, long gia, int ton,
                    int baoHanh, boolean noiBat, boolean banChay) {
        SanPham s = new SanPham();
        s.setTenSanPham(ten);
        s.setMaSanPham(ma);
        s.setThuongHieu(thuongHieu);
        s.setDanhMuc(dm);
        s.setMoTa(moTa);
        s.setGiaCoBan(BigDecimal.valueOf(gia));
        s.setSoLuongTon(ton);
        s.setBaoHanhThang(baoHanh);
        s.setNoiBat(noiBat);
        s.setBanChay(banChay);
        s.setTrangThai(true);
        sanPhamRepository.save(s);
    }
}

package com.example.phukien.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import com.example.phukien.model.DongHoaDon;
import com.example.phukien.model.HoaDon.Loai;
import com.example.phukien.service.HoaDonService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HoaDonController {
    private final HoaDonService dichVu;

    public HoaDonController(HoaDonService dichVu) { this.dichVu = dichVu; }
    private static final String ADMIN = "/admin/don-hang/{id}/hoa-don";
    private static final String KHACH = "/don-hang/{id}/hoa-don";

    @GetMapping("/admin/hoa-don")
    public String traCuu(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "tat-ca") String loc,
                         @RequestParam(defaultValue = "0") int trang, Model model) {
        model.addAttribute("ketQua", dichVu.traCuu(q, loc, trang));
        model.addAttribute("q", q);
        model.addAttribute("loc", loc);
        return "admin/hoa-don-list";
    }

    @GetMapping(ADMIN)
    public String hoSo(@PathVariable int id, Model m) { return hoSo(id, null, m); }

    @GetMapping(KHACH)
    public String hoaDonKhach(@PathVariable int id, Principal p, Model m) { return hoSo(id, p.getName(), m); }

    private String hoSo(int id, String khach, Model m) {
        var hs = dichVu.hoSo(id, khach);
        m.addAttribute("hs", hs);
        m.addAttribute("moi", hs.cacBan().isEmpty() ? null : hs.cacBan().getFirst());
        m.addAttribute("admin", khach == null);
        return "hoa-don-don-hang";
    }
    @GetMapping(ADMIN + "/{ban}")
    public String chiTiet(@PathVariable int id, @PathVariable long ban, @RequestParam(defaultValue = "false") boolean xemTruoc, Model m) {
        return xem(id, ban, null, xemTruoc, m);
    }

    @GetMapping(KHACH + "/{ban}")
    public String xemCuaKhach(@PathVariable int id, @PathVariable long ban, Principal p, Model m) {
        return xem(id, ban, p.getName(), false, m);
    }
    private String xem(int id, long ban, String khach, boolean xemTruoc, Model m) {
        var ct = dichVu.xem(id, ban, khach);
        m.addAttribute("hd", ct.hd());
        m.addAttribute("nd", ct.noiDung());
        m.addAttribute("admin", khach == null);
        return ct.hd().isNhap() && !xemTruoc ? "admin/hoa-don-nhap" : "hoa-don-chung-tu";
    }

    @GetMapping(ADMIN + "/{ban}/xml")
    public ResponseEntity<byte[]> xml(@PathVariable int id, @PathVariable long ban) { return xml(id, ban, null); }

    @GetMapping(KHACH + "/{ban}/xml")
    public ResponseEntity<byte[]> xmlCuaKhach(@PathVariable int id, @PathVariable long ban, Principal p) {
        return xml(id, ban, p.getName());
    }
    private ResponseEntity<byte[]> xml(int id, long ban, String khach) {
        var h = dichVu.taiXml(id, ban, khach);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + h.getSoHoaDon() + ".xml\"")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(h.getXmlPhatHanh().getBytes(StandardCharsets.UTF_8));
    }
    private String veDon(int id) { return "redirect:/admin/don-hang/" + id + "/hoa-don"; }

    @PostMapping(ADMIN)
    public String lap(@PathVariable int id, @RequestParam long tuBan, @RequestParam Loai loai,
                      Principal p, RedirectAttributes ra) {
        try {
            return veDon(id) + "/" + dichVu.lap(id, tuBan, loai, p.getName());
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
            return veDon(id);
        }
    }

    @PostMapping(ADMIN + "/{ban}")
    public String luu(@PathVariable int id, @PathVariable long ban, @RequestParam long lan,
                      @RequestParam MultiValueMap<String,String> form, RedirectAttributes ra) {
        try {
            var mau = new HoaDonService.Mau(form.getFirst("nguoiMua"), form.getFirst("diaChi"), form.getFirst("mst"),
                    form.getFirst("lyDo"), docHang(form), soTien(form.getFirst("giam")));
            dichVu.luu(id, ban, lan, mau);
            return veDon(id) + "/" + ban + "?xemTruoc=true";
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        } catch (NumberFormatException e) {
            ra.addFlashAttribute("loi", "Vui lòng nhập số lượng và số tiền VND hợp lệ.");
        }
        return veDon(id) + "/" + ban;
    }
    private List<DongHoaDon> docHang(MultiValueMap<String,String> form) {
        var ten = form.getOrDefault("ten", List.of());
        var sl = form.getOrDefault("sl", List.of());
        var gia = form.getOrDefault("gia", List.of());
        if (ten.size() != sl.size() || sl.size() != gia.size()) throw new IllegalStateException("Thiếu thông tin dòng hàng.");
        List<DongHoaDon> ds = new ArrayList<>();
        for (int i = 0; i < ten.size(); i++) {
            int n = Integer.parseInt(sl.get(i));
            if (n != 0) ds.add(HoaDonService.dong(ten.get(i), n, soTien(gia.get(i))));
        }
        return ds;
    }
    private BigDecimal soTien(String s) {
        if (s == null) return null;
        if (!s.matches("[0-9]{1,15}")) throw new NumberFormatException();
        return new BigDecimal(s);
    }
    @PostMapping(ADMIN + "/{ban}/phat-hanh")
    public String phatHanh(@PathVariable int id, @PathVariable long ban, @RequestParam long lan,
                           Principal p, RedirectAttributes ra) {
        try {
            dichVu.phatHanh(id, ban, lan, p.getName());
            ra.addFlashAttribute("thongBao", "Đã phát hành hóa đơn điện tử mô phỏng.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return veDon(id) + "/" + ban;
    }
    @PostMapping(ADMIN + "/{ban}/bo-nhap")
    public String boNhap(@PathVariable int id, @PathVariable long ban, @RequestParam long lan, RedirectAttributes ra) {
        try {
            dichVu.boNhap(id, ban, lan);
            ra.addFlashAttribute("thongBao", "Đã bỏ bản nháp.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("loi", e.getMessage());
        }
        return veDon(id);
    }
}

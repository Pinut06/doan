package com.example.phukien.controller.admin;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import com.example.phukien.model.SanPham;
import com.example.phukien.service.BaoCaoService;
import com.example.phukien.service.BaoCaoService.Loai;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class AdminBaoCaoController {
    private final BaoCaoService baoCao;
    public AdminBaoCaoController(BaoCaoService baoCao) { this.baoCao = baoCao; }

    @GetMapping("/admin/bao-cao")
    public String xem(@RequestParam(defaultValue = "doanh-thu") String loai,
                      @RequestParam(defaultValue = "") String tu, @RequestParam(defaultValue = "") String den, Model m) {
        var muc = Arrays.stream(Loai.values()).filter(l -> l.getMa().equals(loai)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var homNay = SanPham.ngayBan();
        String batDau = tu.isBlank() ? homNay.withDayOfMonth(1).toString() : tu;
        String ketThuc = den.isBlank() ? homNay.toString() : den;
        m.addAttribute("cacMuc", Loai.values());
        m.addAttribute("muc", muc);
        m.addAttribute("tu", batDau);
        m.addAttribute("den", ketThuc);
        boolean congNo = muc == Loai.CONG_NO;
        m.addAttribute("congNo", congNo);
        try {
            LocalDate dau = congNo ? homNay : LocalDate.parse(batDau);
            LocalDate cuoi = congNo ? homNay : LocalDate.parse(ketThuc);
            if (dau.isAfter(cuoi) || dau.getYear() < 1000 || cuoi.isAfter(LocalDate.of(9999,12,30)))
                m.addAttribute("loi", "Khoảng ngày không hợp lệ. Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            else m.addAttribute("kq", baoCao.xem(muc, dau, cuoi));
        } catch (DateTimeParseException e) {
            m.addAttribute("loi", "Vui lòng chọn ngày bắt đầu và ngày kết thúc hợp lệ.");
        }
        return "admin/bao-cao";
    }
}

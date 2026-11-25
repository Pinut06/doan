package com.example.phukien.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.phukien.model.MaGiamGia;
import com.example.phukien.model.SanPham;
import com.example.phukien.model.UuDaiDon.Loai;
import com.example.phukien.repository.MaGiamGiaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/khuyen-mai")
public class AdminMaGiamGiaController {
    private final MaGiamGiaRepository cacMa;

    public AdminMaGiamGiaController(MaGiamGiaRepository cacMa) { this.cacMa = cacMa; }

    public record Phieu(Integer id, Long phienBan,
            @NotBlank @Size(max = 120) String ten,
            @NotNull @Pattern(regexp = "[a-zA-Z0-9_-]{1,32}") String ma,
            @NotNull Loai loai,
            @NotNull @Min(1) @Digits(integer = 15, fraction = 0) BigDecimal muc,
            @NotNull @Min(0) @Digits(integer = 15, fraction = 0) BigDecimal nguong,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            boolean kichHoat) {}

    private String trang(Model model) {
        model.addAttribute("cacMa", cacMa.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "admin/ma-giam-gia";
    }

    private MaGiamGia tim(int id) {
        return cacMa.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public String danhSach(@RequestParam(required = false) Integer sua, Model model) {
        MaGiamGia m = sua == null ? null : tim(sua);
        model.addAttribute("phieu", m == null
                ? new Phieu(null, null, "", "", Loai.PHAN_TRAM, null, BigDecimal.ZERO, SanPham.ngayBan(), null, true)
                : new Phieu(m.getId(), m.getPhienBan(), m.getTen(), m.getMa(), m.getLoai(), m.getMuc(),
                            m.getNguong(), m.getTuNgay(), m.getDenNgay(), m.isKichHoat()));
        return trang(model);
    }

    @PostMapping
    public String luu(@Valid @ModelAttribute("phieu") Phieu p, BindingResult ketQua, Model model, RedirectAttributes ra) {
        if (ketQua.hasErrors()) {
            model.addAttribute("loi", "Kiểm tra tên, mã, thời gian và mức giảm. Số tiền phải là số nguyên tối đa 15 chữ số.");
            return trang(model);
        }
        MaGiamGia m = p.id() == null ? new MaGiamGia(p.ma()) : tim(p.id());
        if (p.id() != null && (!Long.valueOf(m.getPhienBan()).equals(p.phienBan()) || !m.getMa().equals(MaGiamGia.chuanHoa(p.ma())))) {
            model.addAttribute("loi", "Mã đã thay đổi. Vui lòng mở lại chương trình để sửa.");
            return trang(model);
        }
        try {
            m.capNhat(p.ten(), p.loai(), p.muc(), p.nguong(), p.tuNgay(), p.denNgay(), p.kichHoat());
            cacMa.saveAndFlush(m);
        } catch (IllegalArgumentException e) {
            model.addAttribute("loi", e.getMessage());
            return trang(model);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("loi", "Mã giảm giá đã tồn tại hoặc điều kiện chưa hợp lệ.");
            return trang(model);
        } catch (OptimisticLockingFailureException e) {
            model.addAttribute("loi", "Chương trình vừa được cập nhật. Vui lòng mở lại để sửa.");
            return trang(model);
        }
        ra.addFlashAttribute("thongBao", "Đã lưu chương trình khuyến mãi.");
        return "redirect:/admin/khuyen-mai";
    }
}

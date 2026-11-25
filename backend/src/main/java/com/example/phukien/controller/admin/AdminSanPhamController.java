package com.example.phukien.controller.admin;

import java.io.IOException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phukien.model.SanPham;
import com.example.phukien.service.DanhMucService;
import com.example.phukien.service.FileStorageService;
import com.example.phukien.service.SanPhamService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/san-pham")
public class AdminSanPhamController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final FileStorageService fileStorageService;

    public AdminSanPhamController(SanPhamService sanPhamService, DanhMucService danhMucService,
                                  FileStorageService fileStorageService) {
        this.sanPhamService = sanPhamService;
        this.danhMucService = danhMucService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("sanPhams", sanPhamService.getAllSanPham());
        return "admin/san-pham-list";
    }

    @GetMapping("/new")
    public String formThem(Model model) {
        model.addAttribute("sanPham", new SanPham());
        model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
        return "admin/san-pham-form";
    }

    @GetMapping("/{id}/edit")
    public String formSua(@PathVariable Integer id, Model model) {
        SanPham sp = sanPhamService.getSanPham(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("sanPham", sp);
        model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
        return "admin/san-pham-form";
    }

    @PostMapping("/save")
    public String luu(@Valid @ModelAttribute("sanPham") SanPham sanPham,
                      BindingResult ketQua,
                      @RequestParam(required = false) Long phienBan,
                      @RequestParam(name = "giaVon", required = false) String giaVonGui,
                      @RequestParam(required = false) Integer danhMucId,
                      @RequestParam(value = "anh", required = false) MultipartFile anh,
                      Model model,
                      RedirectAttributes ra) {

        if (danhMucId != null) {
            sanPham.setDanhMuc(danhMucService.getDanhMuc(danhMucId).orElse(null));
        }
        String anhCu = null;
        if (sanPham.getIdSanPham() != null) {
            SanPham hienTai = sanPhamService.getSanPham(sanPham.getIdSanPham())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            anhCu = hienTai.getHinhAnh();
            if (giaVonGui == null) sanPham.setGiaVon(hienTai.getGiaVon());
            if (phienBan == null) sanPham.setPhienBan(hienTai.getPhienBan());
        }
        sanPham.setHinhAnh(anhCu);

        if (ketQua.hasErrors()) {
            model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
            return "admin/san-pham-form";
        }

        String anhMoi = null;
        try {
            anhMoi = fileStorageService.luu(anh);
            if (anhMoi != null) {
                sanPham.setHinhAnh(anhMoi);
            }
            sanPhamService.save(sanPham);
        } catch (OptimisticLockingFailureException e) {
            fileStorageService.xoa(anhMoi);
            ra.addFlashAttribute("loi", "Giá, tồn kho hoặc thông tin sản phẩm đã thay đổi. Vui lòng mở lại để sửa.");
            return "redirect:/admin/san-pham";
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("loiAnh", e instanceof IllegalArgumentException
                    ? e.getMessage() : "Không lưu được ảnh, vui lòng thử lại.");
            model.addAttribute("danhMucs", danhMucService.getAllDanhMuc());
            return "admin/san-pham-form";
        }

        if (anhMoi != null) fileStorageService.xoa(anhCu);
        ra.addFlashAttribute("thongBao", "Đã lưu sản phẩm \"" + sanPham.getTenSanPham() + "\".");
        return "redirect:/admin/san-pham";
    }

    @PostMapping("/{id}/delete")
    public String xoa(@PathVariable Integer id, RedirectAttributes ra) {
        sanPhamService.getSanPham(id).ifPresent(sp -> {
            try {
                sanPhamService.delete(id);
                fileStorageService.xoa(sp.getHinhAnh());
                ra.addFlashAttribute("thongBao", "Đã xóa sản phẩm \"" + sp.getTenSanPham() + "\".");
            } catch (DataIntegrityViolationException e) {
                ra.addFlashAttribute("loi", "Không thể xóa \"" + sp.getTenSanPham()
                        + "\" vì đã có trong đơn hàng. Hãy bỏ chọn \"Đang bán\" để ẩn sản phẩm.");
            }
        });
        return "redirect:/admin/san-pham";
    }
}

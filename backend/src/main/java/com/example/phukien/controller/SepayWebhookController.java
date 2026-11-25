package com.example.phukien.controller;

import java.util.Map;
import java.math.BigDecimal;
import com.example.phukien.service.ThanhToanService;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.phukien.config.SepayConfig;
import com.example.phukien.service.DonHangService;

@RestController
@RequestMapping("/api/sepay")
public class SepayWebhookController {

    private final DonHangService donHangService;
    private final SepayConfig sepayConfig;
    private final ThanhToanService thanhToan;

    public SepayWebhookController(DonHangService donHangService, SepayConfig sepayConfig, ThanhToanService thanhToan) {
        this.thanhToan = thanhToan;
        this.donHangService = donHangService;
        this.sepayConfig = sepayConfig;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> payload) {

        System.out.println("========== [SEPAY WEBHOOK NHẬN ĐƯỢC GIAO DỊCH MỚI] ==========");
        String configKey = sepayConfig.getApiKey();
        if (configKey == null || configKey.isBlank() || "YOUR_SEPAY_API_KEY_HERE".equals(configKey)
                || !(("Apikey " + configKey).equals(authHeader) || configKey.equals(authHeader))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String taiKhoan = chuoi(payload.get("accountNumber"));
        if (!taiKhoan.isEmpty() && sepayConfig.getAccountNumber() != null && !sepayConfig.getAccountNumber().isBlank()
                && !taiKhoan.equals(sepayConfig.getAccountNumber().strip())) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Different receiving account"));
        }
        String transferType = chuoi(payload.getOrDefault("transferType", "in"));
        if (!"in".equals(transferType)) {
            System.out.println("-> Bỏ qua vì không phải giao dịch tiền vào (transferType = " + transferType + ")");
            return ResponseEntity.ok(Map.of("success", true, "message", "Ignored outgoing transaction"));
        }

        String rawCode = chuoi(payload.get("code"));
        String code = extractMaDon(rawCode);
        if (code == null) {
            String content = chuoi(payload.get("content"));
            code = extractMaDon(content);
        }
        if (code == null) {
            String desc = chuoi(payload.get("description"));
            code = extractMaDon(desc);
        }



        if (code == null || code.isBlank()) {
            System.out.println("-> Không tìm thấy mã đơn hàng nào trong nội dung giao dịch!");
            return ResponseEntity.ok(Map.of("success", true, "message", "No order code found"));
        }

        Object soTien = payload.getOrDefault("transferAmount", payload.getOrDefault("amountIn", payload.get("amount_in")));
        try {
            BigDecimal tien = new BigDecimal(String.valueOf(soTien));
            if (tien.signum() <= 0 || tien.stripTrailingZeros().scale() > 0 || tien.compareTo(BigDecimal.TEN.pow(15)) >= 0)
                throw new NumberFormatException();
            boolean timThay = thanhToan.nhanChuyenKhoan(code, tien, chuoi(payload.get("id")), chuoi(payload.get("referenceCode")));
            return ResponseEntity.ok(Map.of("success", true, "message", timThay ? "Transfer recorded" : "Unknown order"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid transfer amount"));
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("success", false, "message", "Transfer requires reconciliation"));
        }
    }

    private String chuoi(Object value) { return value == null ? "" : value.toString().strip(); }

    @PostMapping("/test-simulate/{maDon}")
    public ResponseEntity<?> testSimulate(@PathVariable String maDon, java.security.Principal principal) {
        if (!sepayConfig.isDemoEnabled() || principal == null) return ResponseEntity.notFound().build();
        String normalized = extractMaDon(maDon);
        if (normalized != null) maDon = normalized;
        boolean updated = donHangService.choMoPhongChuyenKhoan(maDon, principal.getName());
        return ResponseEntity.ok(Map.of("success", updated, "message", "Simulated payment for " + maDon));
    }

    private String extractMaDon(String content) {
        if (content == null || content.isBlank()) return null;
        content = content.toUpperCase();

        var m = java.util.regex.Pattern.compile("(P[GT])\\s*(\\d+)").matcher(content);
        if (!m.find() || m.group(2).length() > 10) return null;
        long id = Long.parseLong(m.group(2));
        return id <= Integer.MAX_VALUE ? m.group(1) + String.format("%06d", id) : null;
    }
}

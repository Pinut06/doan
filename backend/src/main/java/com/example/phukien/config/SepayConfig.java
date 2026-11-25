package com.example.phukien.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình SePay – thông tin ngân hàng nhận tiền và API key xác thực webhook.
 */
@Configuration
@ConfigurationProperties(prefix = "sepay")
public class SepayConfig {

    private String bankName;
    private String bankCode;
    private String accountNumber;
    private String accountName;
    private String apiKey;
    private boolean demoEnabled;

    public boolean isDemoEnabled() { return demoEnabled; }
    public void setDemoEnabled(boolean demoEnabled) { this.demoEnabled = demoEnabled; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    /**
     * Tạo URL ảnh QR VietQR qua qr.sepay.vn.
     * @param amount  số tiền
     * @param content nội dung chuyển khoản (mã đơn hàng)
     */
    public String buildQrUrl(long amount, String content) {
        return String.format(
                "https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%d&des=%s&template=compact",
                accountNumber, bankCode, amount, content);
    }
}

package com.example.phukien.model;

public enum PhuongThucThanhToan {
    COD("Thanh toán khi nhận hàng (COD)"),
    CHUYEN_KHOAN("Chuyển khoản ngân hàng (Quét mã VietQR SePay)"),
    ONLINE("Thanh toán online (demo)");

    private final String nhan;

    PhuongThucThanhToan(String nhan) { this.nhan = nhan; }

    public String getNhan() { return nhan; }
}

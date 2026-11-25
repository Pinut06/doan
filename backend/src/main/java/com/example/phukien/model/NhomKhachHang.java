package com.example.phukien.model;

public enum NhomKhachHang {
    THUONG("Thông thường"),
    THAN_THIET("Thân thiết"),
    VIP("VIP");

    private final String nhan;

    NhomKhachHang(String nhan) { this.nhan = nhan; }

    public String getNhan() { return nhan; }
}

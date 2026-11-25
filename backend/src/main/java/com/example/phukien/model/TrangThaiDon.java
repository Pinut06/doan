package com.example.phukien.model;

import java.util.List;

public enum TrangThaiDon {
    CHO_XAC_NHAN("Chờ xác nhận", "tag-yellow"),
    CHO_HANG("Chờ hàng về (đặt trước)", "tag-blue"),
    DA_XAC_NHAN("Đã xác nhận", "tag-blue"),
    DA_DUYET("Đã duyệt", "tag-green"),
    DANG_GIAO("Đang giao", "tag-blue"),
    HOAN_THANH("Hoàn thành", "tag-green"),
    DA_HUY("Đã hủy", "tag-red");

    private final String nhan;
    private final String mau;

    TrangThaiDon(String nhan, String mau) {
        this.nhan = nhan;
        this.mau = mau;
    }

    public String getNhan() { return nhan; }
    public String getMau() { return mau; }

    public String getHanhDong() {
        return switch (this) {
            case DA_XAC_NHAN -> "Xác nhận đơn với khách";
            case DA_DUYET -> "Duyệt đơn hàng";
            case DANG_GIAO -> "Bàn giao cho đơn vị vận chuyển";
            case HOAN_THANH -> "Xác nhận khách đã nhận hàng";
            case DA_HUY -> "Hủy đơn hàng";
            default -> nhan;
        };
    }

    public List<TrangThaiDon> buocTiepTheo() {
        return switch (this) {
            case CHO_XAC_NHAN, CHO_HANG -> List.of(DA_XAC_NHAN, DA_HUY);
            case DA_XAC_NHAN -> List.of(DA_DUYET, DA_HUY);
            case DA_DUYET -> List.of(DANG_GIAO, DA_HUY);
            case DANG_GIAO -> List.of(HOAN_THANH);
            case HOAN_THANH, DA_HUY -> List.of();
        };
    }
}

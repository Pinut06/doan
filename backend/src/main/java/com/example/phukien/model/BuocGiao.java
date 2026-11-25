package com.example.phukien.model;

public enum BuocGiao {
    CHO_YEU_CAU("Chờ lập yêu cầu xuất"), CHO_XUAT("Đã yêu cầu xuất hàng"),
    CO_PHIEU("Đã lập phiếu giao"), DANG_GIAO("Đang vận chuyển"), THAT_BAI("Giao thất bại"),
    CHO_HOAN("Đang chuyển hoàn"), DA_HOAN("Kho đã nhận lại hàng"), DA_GIAO("Đã giao thành công"),
    DA_HUY("Đã hủy trước khi giao");

    private final String nhan;
    BuocGiao(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
}

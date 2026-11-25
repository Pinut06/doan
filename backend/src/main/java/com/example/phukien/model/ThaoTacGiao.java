package com.example.phukien.model;

import static com.example.phukien.model.BuocGiao.*;

public enum ThaoTacGiao {
    YEU_CAU("Lập yêu cầu xuất hàng", CHO_YEU_CAU, CHO_XUAT, null),
    LAP_PHIEU("Tạo phiếu giao hàng", CHO_XUAT, CO_PHIEU, null),
    BAN_GIAO("Bàn giao vận chuyển", CO_PHIEU, DANG_GIAO, null),
    CAP_NHAT("Cập nhật hành trình", DANG_GIAO, DANG_GIAO, "Vị trí hoặc thông tin vận chuyển"),
    XAC_NHAN("Xác nhận giao thành công", DANG_GIAO, DA_GIAO, "Tên người thực tế nhận hàng"),
    BAO_LOI("Ghi nhận giao thất bại", DANG_GIAO, THAT_BAI, "Lý do giao thất bại"),
    GIAO_LAI("Bắt đầu giao lại", THAT_BAI, DANG_GIAO, "Thông tin lần giao lại"),
    CHUYEN_HOAN("Yêu cầu chuyển hoàn", THAT_BAI, CHO_HOAN, "Lý do chuyển hoàn"),
    NHAN_HOAN("Xác nhận kho nhận đủ hàng để nhập lại", CHO_HOAN, DA_HOAN, "Kết quả kiểm đếm hàng hoàn");

    private final String nhan;
    private final BuocGiao tu;
    private final BuocGiao den;
    private final String nhanGhiChu;
    ThaoTacGiao(String nhan, BuocGiao tu, BuocGiao den, String nhanGhiChu) {
        this.nhan = nhan;
        this.tu = tu;
        this.den = den;
        this.nhanGhiChu = nhanGhiChu;
    }
    public String getNhan() { return nhan; }
    public String getNhanGhiChu() { return nhanGhiChu; }
    public BuocGiao getTu() { return tu; }
    public BuocGiao getDen() { return den; }
}

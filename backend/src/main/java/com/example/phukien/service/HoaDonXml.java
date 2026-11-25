package com.example.phukien.service;

import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.example.phukien.model.HoaDon;

final class HoaDonXml {
    private HoaDonXml() {}

    static String tao(HoaDon h, HoaDonService.NoiDung nd) {
        try {
            var out = new StringWriter();
            var x = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
            x.writeStartDocument("UTF-8", "1.0");
            x.writeStartElement("HoaDonMoPhong");
            x.writeAttribute("phienBan", "1");
            ghi(x, "PhamVi", "Chứng từ mô phỏng dùng trong đồ án");
            ghi(x, "SoHoaDon", h.getSoHoaDon());
            ghi(x, "Loai", h.getLoai().name());
            ghi(x, "MaDon", h.getMaDon());
            ghi(x, "NgayPhatHanh", h.getNgayPhatHanh().atOffset(java.time.ZoneOffset.ofHours(7)).toString());
            ghi(x, "NguoiPhatHanh", h.getNguoiPhatHanh());
            ghi(x, "BenBan", "PeanutGear");
            ghi(x, "BenMua", h.getNguoiMua());
            ghi(x, "DiaChiMua", h.getDiaChi());
            ghi(x, "MaSoThueMua", h.getMst());
            ghi(x, "HoaDonThamChieu", h.getSoThamChieu());
            ghi(x, "LyDo", h.getLyDo());
            ghi(x, "TienTe", "VND");
            x.writeStartElement("ChiTiet");
            for (var d : nd.dongs()) {
                x.writeStartElement("Dong");
                ghi(x, "TenHang", d.getTen());
                ghi(x, "SoLuong", Integer.toString(d.getSoLuong()));
                ghi(x, "DonGia", d.getDonGia().toPlainString());
                ghi(x, "ThanhTien", d.getThanhTien().toPlainString());
                x.writeEndElement();
            }
            x.writeEndElement();
            ghi(x, "GiamTru", nd.giam().toPlainString());
            ghi(x, "TongChungTu", nd.phaiTra().toPlainString());
            ghi(x, "GiaTriSauXuLy", h.getTong().toPlainString());
            x.writeEndElement();
            x.writeEndDocument();
            x.close();
            return out.toString();
        } catch (XMLStreamException e) {
            throw new IllegalStateException("Chưa thể tạo tệp hóa đơn điện tử.", e);
        }
    }
    private static void ghi(XMLStreamWriter x, String ten, String value) throws XMLStreamException {
        if (value == null || value.isBlank()) return;
        x.writeStartElement(ten);
        x.writeCharacters(value);
        x.writeEndElement();
    }
}

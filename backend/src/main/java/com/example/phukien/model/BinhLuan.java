package com.example.phukien.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "binh_luan")
public class BinhLuan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "binh_luan_id")
    private Integer idBinhLuan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;

    @ManyToOne(optional = false)
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;

    @Column(name = "noi_dung", nullable = false, length = 1000)
    private String noiDung;

    @Column(name = "so_sao")
    private Integer soSao = 5;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    public BinhLuan() {
    }

    public Integer getIdBinhLuan() { return idBinhLuan; }
    public void setIdBinhLuan(Integer idBinhLuan) { this.idBinhLuan = idBinhLuan; }
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public Integer getSoSao() { return soSao; }
    public void setSoSao(Integer soSao) { this.soSao = soSao; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}

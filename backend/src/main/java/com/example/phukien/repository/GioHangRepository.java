package com.example.phukien.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.phukien.model.GioHang;

public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    interface DongDat {
        Integer getSanPhamId();
        Integer getSoLuong();
    }

    @Query("select g.sanPham.idSanPham as sanPhamId, g.soLuong as soLuong from GioHang g where g.khachHang.idKhachHang = :id order by g.sanPham.idSanPham")
    List<DongDat> docDeDat(@Param("id") Integer id);

    List<GioHang> findByKhachHang_IdKhachHangOrderByIdGioHangAsc(Integer khachHangId);

    Optional<GioHang> findByKhachHang_IdKhachHangAndSanPham_IdSanPham(Integer khachHangId, Integer sanPhamId);

    @Query("select coalesce(sum(g.soLuong), 0) from GioHang g where g.khachHang.idKhachHang = :id")
    long tongSoLuong(@Param("id") Integer khachHangId);

    @Modifying
    @Query("delete from GioHang g where g.khachHang.idKhachHang = :id")
    void xoaTheoKhachHang(@Param("id") Integer khachHangId);

    @Modifying
    @Query("delete from GioHang g where g.sanPham.idSanPham = :id")
    void xoaTheoSanPham(@Param("id") Integer sanPhamId);
}

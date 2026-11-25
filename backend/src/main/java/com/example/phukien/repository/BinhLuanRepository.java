package com.example.phukien.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.phukien.model.BinhLuan;

public interface BinhLuanRepository extends JpaRepository<BinhLuan, Integer> {

    List<BinhLuan> findBySanPham_IdSanPhamOrderByIdBinhLuanDesc(Integer sanPhamId);

    @Query("select avg(b.soSao) from BinhLuan b where b.sanPham.idSanPham = :id")
    Double diemTrungBinh(@Param("id") Integer sanPhamId);

    @Modifying
    @Query("delete from BinhLuan b where b.sanPham.idSanPham = :id")
    void xoaTheoSanPham(@Param("id") Integer sanPhamId);

    @Modifying
    @Query("delete from BinhLuan b where b.khachHang.idKhachHang = :id")
    void xoaTheoKhachHang(@Param("id") Integer khachHangId);
}

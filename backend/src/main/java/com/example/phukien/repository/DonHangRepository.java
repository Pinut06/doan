package com.example.phukien.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.phukien.model.DonHang;
import com.example.phukien.model.TrangThaiDon;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DonHang d where d.idDonHang = :id")
    Optional<DonHang> findByIdForUpdate(@Param("id") Integer id);

    List<DonHang> findByKhachHang_IdKhachHangOrderByIdDonHangDesc(Integer khachHangId);

    List<DonHang> findAllByOrderByIdDonHangDesc();

    long countByTrangThai(TrangThaiDon trangThai);

    List<DonHang> findByMaYeuCauInOrderByIdDonHangAsc(List<String> ma);

    @Query("""
            select d from DonHang d where (:kh is null or d.khachHang.idKhachHang = :kh)
                and (:tt is null or d.trangThai = :tt)
                and (:q = '' or locate(lower(:q), lower(d.maDon)) > 0)
            order by d.idDonHang desc
            """)
    List<DonHang> traCuu(@Param("kh") Integer kh, @Param("tt") TrangThaiDon tt, @Param("q") String q);

    @Query("""
            select d from DonHang d join fetch d.khachHang k
            where :q = '' or locate(lower(:q), lower(d.maDon)) > 0
                or locate(lower(:q), lower(k.tenDangNhap)) > 0 or locate(lower(:q), lower(k.hoTen)) > 0
            order by d.idDonHang desc
            """)
    List<DonHang> traThanhToan(@Param("q") String q);

    /** Tìm đơn hàng theo mã đơn (PG000001, PT000001...) */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DonHang> findByMaDon(String maDon);
}

package com.example.phukien.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.phukien.model.SanPham;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query("""
            select s from SanPham s
            where (:chiDangBan = false or s.trangThai = true)
              and (:dm is null or s.danhMuc.idDanhMuc = :dm)
              and (:q = '' or locate(lower(:q), lower(s.tenSanPham)) > 0
                   or locate(lower(:q), lower(s.maSanPham)) > 0
                   or locate(lower(:q), lower(s.thuongHieu)) > 0)
              and (:ton = '' or (:ton = 'con' and s.soLuongTon > 0)
                             or (:ton = 'sap-het' and s.soLuongTon between 1 and 5)
                             or (:ton = 'het' and coalesce(s.soLuongTon, 0) <= 0))
            order by s.idSanPham desc
            """)
    List<SanPham> traCuu(@Param("dm") Integer danhMucId, @Param("q") String tuKhoa,
                         @Param("chiDangBan") boolean chiDangBan, @Param("ton") String ton);

    List<SanPham> findByTrangThaiTrueAndNoiBatTrueOrderByIdSanPhamDesc();

    List<SanPham> findTop4ByDanhMuc_IdDanhMucAndIdSanPhamNotAndTrangThaiTrue(Integer danhMucId, Integer idSanPham);

    long countByDanhMuc_IdDanhMuc(Integer danhMucId);

    List<SanPham> findBySoLuongTonLessThanEqualOrderBySoLuongTonAsc(Integer soLuong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SanPham s where s.idSanPham = :id")
    Optional<SanPham> findByIdForUpdate(@Param("id") Integer id);

    @org.springframework.data.jpa.repository.Modifying
    @Query("update SanPham s set s.soLuongTon = s.soLuongTon + :sl, s.phienBan = s.phienBan + 1 where s.idSanPham = :id")
    void hoanTon(@Param("id") Integer id, @Param("sl") int soLuong);

    @org.springframework.data.jpa.repository.Modifying
    @Query("""
            update SanPham s set s.soLuongTon = s.soLuongTon - :sl, s.phienBan = s.phienBan + 1
            where s.idSanPham = :id and s.trangThai = true and s.soLuongTon >= :sl
            """)
    int giuTon(@Param("id") int id, @Param("sl") int soLuong);
}

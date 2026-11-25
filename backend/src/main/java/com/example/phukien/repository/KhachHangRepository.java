package com.example.phukien.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.phukien.model.KhachHang;
import com.example.phukien.model.NhomKhachHang;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select k from KhachHang k where k.idKhachHang = :id")
    Optional<KhachHang> khoa(@Param("id") Integer id);

    Optional<KhachHang> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCaseAndIdKhachHangNot(String email, Integer id);

    @Query("""
            select k from KhachHang k
            where (:nhom is null or k.nhomKhachHang = :nhom)
              and (:q = '' or locate(lower(:q), lower(k.tenDangNhap)) > 0
                           or locate(lower(:q), lower(k.hoTen)) > 0
                           or locate(lower(:q), lower(k.email)) > 0
                           or locate(:q, k.soDienThoai) > 0)
            order by k.idKhachHang desc
            """)
    List<KhachHang> traCuu(@Param("q") String tuKhoa, @Param("nhom") NhomKhachHang nhom);
}

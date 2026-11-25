package com.example.phukien.repository;

import java.util.List;
import java.util.Optional;
import com.example.phukien.model.YeuCauDoiTra;
import com.example.phukien.model.YeuCauDoiTra.Buoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface YeuCauDoiTraRepository extends JpaRepository<YeuCauDoiTra, Long> {
    List<YeuCauDoiTra> findByChiTiet_DonHang_IdDonHangOrderByIdDesc(int donId);
    Optional<YeuCauDoiTra> findByMaGui(String maGui);

    @Query("""
            select y from YeuCauDoiTra y join y.chiTiet c join c.donHang d
            where (:buoc is null or y.buoc = :buoc) and
                  (:q = '' or locate(lower(:q),lower(d.maDon)) > 0 or locate(lower(:q),lower(c.tenSanPham)) > 0)
            order by y.id desc
            """)
    Page<YeuCauDoiTra> traCuu(String q, Buoc buoc, Pageable trang);
}

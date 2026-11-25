package com.example.phukien.repository;

import java.util.List;
import java.util.Optional;
import com.example.phukien.model.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    List<HoaDon> findByDonIdOrderByLanLapDesc(int id);
    Optional<HoaDon> findByDonIdAndId(int donId, long id);
    Optional<HoaDon> findFirstByDonIdOrderByLanLapDesc(int donId);

    @Query("""
            select h from HoaDon h where
              (:q = '' or locate(lower(:q),lower(h.maDon)) > 0 or locate(lower(:q),lower(h.soHoaDon)) > 0
               or locate(lower(:q),lower(h.nguoiMua)) > 0 or locate(lower(:q),lower(h.mst)) > 0)
              and (:loc = 'tat-ca' or (:loc = 'nhap' and h.soHoaDon is null)
               or (:loc = 'phat-hanh' and h.soHoaDon is not null and h.thayBoi is null)
               or (:loc = 'thay-the' and h.thayBoi is not null))
              order by h.id desc
            """)
    Page<HoaDon> traCuu(String q, String loc, Pageable trang);
}

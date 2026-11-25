package com.example.phukien.repository;

import java.util.Optional;
import com.example.phukien.model.MaGiamGia;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Integer> {
    Optional<MaGiamGia> findByMa(String ma);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MaGiamGia m where m.ma = :ma")
    Optional<MaGiamGia> khoaTheoMa(String ma);
}

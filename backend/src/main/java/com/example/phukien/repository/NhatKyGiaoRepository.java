package com.example.phukien.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.phukien.model.NhatKyGiao;

public interface NhatKyGiaoRepository extends JpaRepository<NhatKyGiao, Long> {
    List<NhatKyGiao> findByGiaoHang_IdOrderByIdDesc(int id);
}

package com.example.phukien.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.example.phukien.model.PhieuThanhToan;

public interface PhieuThanhToanRepository extends JpaRepository<PhieuThanhToan, Long> {
    List<PhieuThanhToan> findByDon_IdDonHangOrderByIdDesc(int id);
    Optional<PhieuThanhToan> findByYeuCau(String yeuCau);
    Optional<PhieuThanhToan> findByDoiSoat(String doiSoat);
    @EntityGraph(attributePaths = "don")
    Optional<PhieuThanhToan> findByIdAndDon_IdDonHang(long id, int donId);
}

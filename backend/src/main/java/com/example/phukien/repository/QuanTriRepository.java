package com.example.phukien.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.phukien.model.QuanTri;

public interface QuanTriRepository extends JpaRepository<QuanTri, Integer> {

    Optional<QuanTri> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);
}

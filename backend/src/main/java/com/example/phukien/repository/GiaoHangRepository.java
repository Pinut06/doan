package com.example.phukien.repository;

import java.util.Optional;
import com.example.phukien.model.GiaoHang;
import com.example.phukien.model.BuocGiao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GiaoHangRepository extends JpaRepository<GiaoHang, Integer> {
    Optional<GiaoHang> findByDon_IdDonHang(int id);

    interface Dong {
        Integer getDonId();
        String getMaDon();
        String getNguoiNhan();
        String getDonVi();
        String getVanDon();
        String getMaBuoc();
        default String getNhan() { return BuocGiao.valueOf(getMaBuoc()).getNhan(); }
    }

    String BUOC = "coalesce(g.buoc, case d.trang_thai when 'DANG_GIAO' then 'DANG_GIAO' when 'HOAN_THANH' then 'DA_GIAO' else 'CHO_YEU_CAU' end)";
    String LOC = """
             from don_hang d left join giao_hang g on g.don_hang_id = d.don_hang_id
             where (g.id is not null or d.trang_thai in ('DA_DUYET','DANG_GIAO','HOAN_THANH'))
               and (:q = '' or locate(:q, d.ma_don)>0 or locate(:q, g.ma_van_don)>0 or locate(:q, d.ho_ten_nhan)>0)
               and (:buoc = '' or :buoc = """ + " " + BUOC + ")";
    @Query(value = "select d.don_hang_id as donId, d.ma_don as maDon, d.ho_ten_nhan as nguoiNhan, "
            + "g.don_vi as donVi, g.ma_van_don as vanDon, " + BUOC + " as maBuoc " + LOC + " order by d.don_hang_id desc",
           countQuery = "select count(*) " + LOC, nativeQuery = true)
    Page<Dong> traCuu(String q, String buoc, Pageable page);
}

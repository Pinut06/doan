-- Chạy sau các nâng cấp thanh toán, giao hàng và đổi trả.
SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='san_pham' AND column_name='gia_von'), 'SELECT 1', 'ALTER TABLE san_pham ADD COLUMN gia_von decimal(15,0) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='chi_tiet_don_hang' AND column_name='gia_von_khi_ban'), 'SELECT 1', 'ALTER TABLE chi_tiet_don_hang ADD COLUMN gia_von_khi_ban decimal(15,0) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='yeu_cau_doi_tra' AND column_name='gia_von_hang_doi'), 'SELECT 1', 'ALTER TABLE yeu_cau_doi_tra ADD COLUMN gia_von_hang_doi decimal(15,0) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='don_hang' AND column_name='nhan_vien_ban'), 'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN nhan_vien_ban varchar(50) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='don_hang' AND column_name='ngay_hoan_thanh'), 'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN ngay_hoan_thanh datetime(6) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='don_hang' AND index_name='idx_bc_giao'), 'SELECT 1', 'CREATE INDEX idx_bc_giao ON don_hang (ngay_hoan_thanh)');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='don_hang' AND index_name='idx_bc_dat'), 'SELECT 1', 'CREATE INDEX idx_bc_dat ON don_hang (ngay_tao)');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='yeu_cau_doi_tra' AND index_name='idx_bc_tra'), 'SELECT 1', 'CREATE INDEX idx_bc_tra ON yeu_cau_doi_tra (ngay_xu_ly)');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema=DATABASE() AND table_name='phieu_thanh_toan' AND index_name='idx_bc_phieu'), 'SELECT 1', 'CREATE INDEX idx_bc_phieu ON phieu_thanh_toan (ngay_lap)');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

-- Chỉ lấy ngày giao từ sự kiện xác nhận thành công đã được ghi.
UPDATE don_hang d
JOIN (SELECT g.don_hang_id, MAX(n.thoi_diem) AS ngay
      FROM giao_hang g JOIN nhat_ky_giao n ON n.giao_hang_id = g.id
      WHERE n.su_kien = 'Xác nhận giao thành công'
      GROUP BY g.don_hang_id) cu ON cu.don_hang_id = d.don_hang_id
SET d.ngay_hoan_thanh = cu.ngay
WHERE d.trang_thai = 'HOAN_THANH' AND d.ngay_hoan_thanh IS NULL;

-- Chạy trên database của cửa hàng trước khi khởi động phiên bản mới.
CREATE TABLE IF NOT EXISTS `giao_hang` (
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  don_hang_id int NOT NULL UNIQUE,
  buoc varchar(20) NOT NULL,
  lan_cap_nhat bigint NOT NULL DEFAULT 0,
  du_lieu_cu bit(1) NOT NULL DEFAULT b'0',
  don_vi varchar(100) DEFAULT NULL,
  ma_van_don varchar(80) DEFAULT NULL,
  ngay_yeu_cau datetime(6) DEFAULT NULL,
  ngay_lap_phieu datetime(6) DEFAULT NULL,
  nguoi_lap_phieu varchar(255) DEFAULT NULL,
  thu_ho_khi_lap decimal(15,0) DEFAULT NULL,
  CONSTRAINT uq_giao_van_don UNIQUE (don_vi, ma_van_don),
  FOREIGN KEY (don_hang_id) REFERENCES don_hang(don_hang_id),
  CHECK (buoc IN ('CHO_XUAT','CO_PHIEU','DANG_GIAO','THAT_BAI','CHO_HOAN','DA_HOAN','DA_GIAO','DA_HUY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `nhat_ky_giao` (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  giao_hang_id int NOT NULL,
  thoi_diem datetime(6) NOT NULL,
  nguoi_ghi varchar(255) NOT NULL,
  su_kien varchar(100) NOT NULL,
  noi_dung varchar(500) NOT NULL,
  KEY idx_nk_giao (giao_hang_id, id),
  FOREIGN KEY (giao_hang_id) REFERENCES giao_hang(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

START TRANSACTION;
-- Đơn cũ chỉ có trạng thái đơn. Không suy diễn ngày bàn giao, mã vận đơn hay phiếu xuất.
INSERT INTO giao_hang (don_hang_id, buoc, du_lieu_cu)
SELECT d.don_hang_id, IF(d.trang_thai = 'HOAN_THANH', 'DA_GIAO', 'DANG_GIAO'), 1
FROM don_hang d
WHERE d.trang_thai IN ('DANG_GIAO','HOAN_THANH')
  AND NOT EXISTS (SELECT 1 FROM giao_hang g WHERE g.don_hang_id = d.don_hang_id);
COMMIT;

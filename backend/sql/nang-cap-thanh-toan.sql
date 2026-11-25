-- Chạy trong database của cửa hàng trước khi khởi động phiên bản mới.
SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'tong_thu'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN tong_thu decimal(19,0) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'tong_hoan'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN tong_hoan decimal(19,0) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

SET @ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'giam_sau_ban'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN giam_sau_ban decimal(15,0) DEFAULT NULL');
PREPARE sua FROM @ddl;
EXECUTE sua;
DEALLOCATE PREPARE sua;

CREATE TABLE IF NOT EXISTS `phieu_thanh_toan` (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  don_hang_id int NOT NULL,
  loai varchar(12) NOT NULL,
  hinh_thuc varchar(20) NOT NULL,
  so_tien decimal(15,0) NOT NULL,
  giam_sau_ban decimal(15,0) NOT NULL DEFAULT 0,
  yeu_cau varchar(120) NOT NULL UNIQUE,
  doi_soat varchar(110) DEFAULT NULL UNIQUE,
  nguoi_lap varchar(255) NOT NULL,
  nguoi_nop_nhan varchar(255) NOT NULL,
  noi_dung varchar(255) NOT NULL,
  ngay_lap datetime(6) NOT NULL,
  KEY idx_phieu_don (don_hang_id,id),
  FOREIGN KEY (don_hang_id) REFERENCES don_hang (don_hang_id),
  CHECK (loai IN ('THU','HOAN') AND so_tien > 0),
  CHECK (giam_sau_ban BETWEEN 0 AND so_tien AND (loai = 'HOAN' OR giam_sau_ban = 0)),
  CHECK (hinh_thuc IN ('TIEN_MAT','CHUYEN_KHOAN','SO_DU_DAU'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

START TRANSACTION;
-- Số dư đầu kỳ phản ánh trạng thái cũ, thời điểm lập là thời điểm chuyển đổi.
INSERT INTO phieu_thanh_toan (don_hang_id, loai, hinh_thuc, so_tien, yeu_cau, nguoi_lap,
    nguoi_nop_nhan, noi_dung, ngay_lap, giam_sau_ban)
SELECT d.don_hang_id, 'THU', 'SO_DU_DAU', d.tong_tien, CONCAT('CU:',d.don_hang_id),
    'Hệ thống', k.ten_dang_nhap, 'Chuyển số dư từ trạng thái đã thanh toán cũ', UTC_TIMESTAMP() + INTERVAL 7 HOUR, 0
FROM don_hang d JOIN khach_hang k ON k.khach_hang_id = d.khach_hang_id
WHERE d.tong_thu IS NULL AND d.da_thanh_toan = 1 AND d.tong_tien > 0
  AND NOT EXISTS (SELECT 1 FROM phieu_thanh_toan p WHERE p.don_hang_id = d.don_hang_id);

UPDATE don_hang SET tong_thu = IF(da_thanh_toan = 1, COALESCE(tong_tien,0), 0),
    tong_hoan = 0, giam_sau_ban = 0 WHERE tong_thu IS NULL;
COMMIT;

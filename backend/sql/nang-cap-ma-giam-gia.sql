-- Chạy trong database của ứng dụng trước khi khởi động phiên bản mới.
-- Các đơn đã lập giữ nguyên tổng tiền; cột khuyến mãi của chúng để NULL.
CREATE TABLE IF NOT EXISTS `ma_giam_gia` (
  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `phien_ban` bigint NOT NULL DEFAULT 0,
  `ma` varchar(32) NOT NULL UNIQUE,
  `ten` varchar(120) NOT NULL,
  `loai` varchar(16) NOT NULL,
  `muc` decimal(15,0) NOT NULL,
  `nguong` decimal(15,0) NOT NULL DEFAULT 0,
  `tu_ngay` date NOT NULL,
  `den_ngay` date NOT NULL,
  `kich_hoat` bit(1) NOT NULL DEFAULT b'1',
  CHECK (`loai` IN ('PHAN_TRAM', 'SO_TIEN')),
  CHECK (`muc` >= 1 AND (`loai` <> 'PHAN_TRAM' OR `muc` <= 100)),
  CHECK (`nguong` >= 0 AND `tu_ngay` <= `den_ngay`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @ddl_ma = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'ma_giam_gia'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN ma_giam_gia varchar(32) DEFAULT NULL');
PREPARE nang_cap FROM @ddl_ma;
EXECUTE nang_cap;
DEALLOCATE PREPARE nang_cap;

SET @ddl_ma = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'loai_giam'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN loai_giam varchar(16) DEFAULT NULL');
PREPARE nang_cap FROM @ddl_ma;
EXECUTE nang_cap;
DEALLOCATE PREPARE nang_cap;

SET @ddl_ma = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'muc_giam'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN muc_giam decimal(15,0) DEFAULT NULL');
PREPARE nang_cap FROM @ddl_ma;
EXECUTE nang_cap;
DEALLOCATE PREPARE nang_cap;

SET @ddl_ma = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'nguong_ap_dung'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN nguong_ap_dung decimal(15,0) DEFAULT NULL');
PREPARE nang_cap FROM @ddl_ma;
EXECUTE nang_cap;
DEALLOCATE PREPARE nang_cap;

SET @ddl_ma = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'chiet_khau'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN chiet_khau decimal(15,0) DEFAULT NULL');
PREPARE nang_cap FROM @ddl_ma;
EXECUTE nang_cap;
DEALLOCATE PREPARE nang_cap;

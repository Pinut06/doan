-- Hóa đơn mô phỏng cho đồ án. Đơn cũ chỉ phát sinh hóa đơn khi nhân viên lập.
CREATE TABLE IF NOT EXISTS `hoa_don` (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  don_hang_id int NOT NULL,
  ma_don varchar(30) NOT NULL,
  lan_lap int NOT NULL,
  lan_sua bigint NOT NULL DEFAULT 0,
  loai varchar(16) NOT NULL,
  ban_truoc_id bigint DEFAULT NULL,
  so_tham_chieu varchar(40) DEFAULT NULL,
  so_hoa_don varchar(40) DEFAULT NULL UNIQUE,
  thay_boi varchar(40) DEFAULT NULL,
  nguoi_mua varchar(255) NOT NULL,
  dia_chi varchar(500) NOT NULL,
  mst varchar(30) DEFAULT NULL,
  ly_do varchar(500) DEFAULT NULL,
  nguoi_lap varchar(255) NOT NULL,
  ngay_lap datetime(6) NOT NULL,
  nguoi_phat_hanh varchar(255) DEFAULT NULL,
  ngay_phat_hanh datetime(6) DEFAULT NULL,
  giam decimal(15,0) NOT NULL,
  xml_phat_hanh mediumtext DEFAULT NULL,
  UNIQUE KEY uq_hd_don_lan (don_hang_id, lan_lap),
  FOREIGN KEY (don_hang_id) REFERENCES don_hang(don_hang_id),
  FOREIGN KEY (ban_truoc_id) REFERENCES hoa_don(id),
  CHECK (loai IN ('BAN_HANG','DIEU_CHINH','THAY_THE')),
  CHECK (lan_lap > 0 AND lan_sua >= 0 AND giam >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `dong_hoa_don` (
  hoa_don_id bigint NOT NULL,
  vi_tri int NOT NULL,
  ten_hang varchar(255) NOT NULL,
  so_luong int NOT NULL,
  don_gia decimal(15,0) NOT NULL,
  PRIMARY KEY (hoa_don_id, vi_tri),
  FOREIGN KEY (hoa_don_id) REFERENCES hoa_don(id),
  CHECK (so_luong > 0 AND don_gia >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

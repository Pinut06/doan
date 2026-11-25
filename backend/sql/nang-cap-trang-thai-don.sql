-- Chạy trên database hiện có trước khi dùng trạng thái Đã duyệt.
-- Giữ nguyên thứ tự và giá trị của các trạng thái cũ.
ALTER TABLE don_hang MODIFY COLUMN trang_thai
    ENUM('CHO_HANG','CHO_XAC_NHAN','DANG_GIAO','DA_HUY','DA_XAC_NHAN','HOAN_THANH','DA_DUYET') NOT NULL;

SET @ddl_don = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'phien_ban'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN phien_ban BIGINT NOT NULL DEFAULT 0');
PREPARE cap_nhat_don FROM @ddl_don;
EXECUTE cap_nhat_don;
DEALLOCATE PREPARE cap_nhat_don;

SET @ddl_don = IF(EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'don_hang' AND column_name = 'ma_yeu_cau'),
    'SELECT 1', 'ALTER TABLE don_hang ADD COLUMN ma_yeu_cau VARCHAR(40) DEFAULT NULL UNIQUE');
PREPARE cap_nhat_don FROM @ddl_don;
EXECUTE cap_nhat_don;
DEALLOCATE PREPARE cap_nhat_don;

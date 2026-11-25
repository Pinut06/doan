-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 12, 2026 at 04:33 PM
-- Server version: 10.11.16-MariaDB
-- PHP Version: 8.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `phu_kien_cong_nghe`
--

-- --------------------------------------------------------

--
-- Table structure for table `binh_luan`
--

CREATE TABLE `binh_luan` (
  `binh_luan_id` int(11) NOT NULL,
  `ngay_tao` datetime(6) DEFAULT NULL,
  `noi_dung` varchar(1000) NOT NULL,
  `so_sao` int(11) DEFAULT NULL,
  `khach_hang_id` int(11) NOT NULL,
  `san_pham_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `binh_luan`
--

INSERT INTO `binh_luan` (`binh_luan_id`, `ngay_tao`, `noi_dung`, `so_sao`, `khach_hang_id`, `san_pham_id`) VALUES
(1, '2026-11-12 22:28:09.000000', 'oke đó', 5, 3, 40);

-- --------------------------------------------------------

--
-- Table structure for table `chi_tiet_don_hang`
--

CREATE TABLE `chi_tiet_don_hang` (
  `chi_tiet_id` int(11) NOT NULL,
  `don_gia` decimal(15,0) DEFAULT NULL,
  `so_luong` int(11) NOT NULL,
  `ten_san_pham` varchar(255) NOT NULL,
  `don_hang_id` int(11) NOT NULL,
  `san_pham_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chi_tiet_don_hang`
--

INSERT INTO `chi_tiet_don_hang` (`chi_tiet_id`, `don_gia`, `so_luong`, `ten_san_pham`, `don_hang_id`, `san_pham_id`) VALUES
(1, 3890000, 1, 'Bàn phím HE Logitech PRO X TKL RAPID - Black', 1, 43),
(2, 1650000, 1, 'Bàn di chuột Pulsar PRX Edition Mousepad', 1, 20);

-- --------------------------------------------------------

--
-- Table structure for table `danh_muc`
--

CREATE TABLE `danh_muc` (
  `danh_muc_id` int(11) NOT NULL,
  `ten_danh_muc` varchar(255) NOT NULL,
  `mo_ta` varchar(255) DEFAULT NULL,
  `hinh_anh` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `danh_muc`
--

INSERT INTO `danh_muc` (`danh_muc_id`, `ten_danh_muc`, `mo_ta`, `hinh_anh`) VALUES
(1, 'Tai nghe', 'Tai nghe có dây, không dây, chống ồn', NULL),
(2, 'Sạc & Cáp', 'Củ sạc nhanh, cáp USB-C, Lightning', NULL),
(3, 'Chuột', 'Chuột không dây, chuột gaming, bàn di chuột', NULL),
(4, 'Bàn phím', 'Bàn phím cơ, bàn phím không dây, combo chuột phím', NULL),
(5, 'Pad Chuột', 'Pad văn phòng, Pad Gaming', ''),
(6, 'Pin dự phòng', 'Pin sạc dự phòng dung lượng cao', NULL),
(7, 'Màn hình máy tính', 'Màn hình gaming, thiết kế 3D', '');

-- --------------------------------------------------------

--
-- Table structure for table `don_hang`
--

CREATE TABLE `don_hang` (
  `don_hang_id` int(11) NOT NULL,
  `phien_ban` bigint NOT NULL DEFAULT 0,
  `ma_yeu_cau` varchar(40) DEFAULT NULL UNIQUE,
  `ma_giam_gia` varchar(32) DEFAULT NULL,
  `loai_giam` varchar(16) DEFAULT NULL,
  `muc_giam` decimal(15,0) DEFAULT NULL,
  `nguong_ap_dung` decimal(15,0) DEFAULT NULL,
  `chiet_khau` decimal(15,0) DEFAULT NULL,
  `da_thanh_toan` bit(1) DEFAULT NULL,
  `dat_truoc` bit(1) DEFAULT NULL,
  `dia_chi` varchar(500) NOT NULL,
  `ghi_chu` varchar(500) DEFAULT NULL,
  `ho_ten_nhan` varchar(255) NOT NULL,
  `ma_don` varchar(30) DEFAULT NULL,
  `ngay_tao` datetime(6) DEFAULT NULL,
  `phuong_thuc` enum('CHUYEN_KHOAN','COD','ONLINE') NOT NULL,
  `so_dien_thoai` varchar(20) NOT NULL,
  `tong_tien` decimal(15,0) DEFAULT NULL,
  `tong_thu` decimal(19,0) DEFAULT NULL,
  `tong_hoan` decimal(19,0) DEFAULT NULL,
  `giam_sau_ban` decimal(15,0) DEFAULT NULL,
  `trang_thai` enum('CHO_HANG','CHO_XAC_NHAN','DANG_GIAO','DA_HUY','DA_XAC_NHAN','HOAN_THANH','DA_DUYET') NOT NULL,
  `khach_hang_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `don_hang`
--

INSERT INTO `don_hang` (`don_hang_id`, `da_thanh_toan`, `dat_truoc`, `dia_chi`, `ghi_chu`, `ho_ten_nhan`, `ma_don`, `ngay_tao`, `phuong_thuc`, `so_dien_thoai`, `tong_tien`, `trang_thai`, `khach_hang_id`) VALUES
(1, b'0', b'0', '123', 'cc', 'lý kỳ lạc', 'PG000001', '2026-11-12 22:29:14.000000', 'CHUYEN_KHOAN', '0707172744', 5540000, 'CHO_XAC_NHAN', 3);

-- --------------------------------------------------------

--
-- Table structure for table `gio_hang`
--

CREATE TABLE `gio_hang` (
  `gio_hang_id` int(11) NOT NULL,
  `so_luong` int(11) NOT NULL,
  `khach_hang_id` int(11) NOT NULL,
  `san_pham_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `gio_hang`
--

INSERT INTO `gio_hang` (`gio_hang_id`, `so_luong`, `khach_hang_id`, `san_pham_id`) VALUES
(3, 1, 3, 32),
(5, 1, 3, 28);

-- --------------------------------------------------------

--
-- Table structure for table `khach_hang`
--

CREATE TABLE `khach_hang` (
  `khach_hang_id` int(11) NOT NULL,
  `ten_dang_nhap` varchar(50) NOT NULL,
  `mat_khau` varchar(255) NOT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `so_dien_thoai` varchar(20) DEFAULT NULL,
  `dia_chi` varchar(500) DEFAULT NULL,
  `nhom_khach_hang` varchar(20) NOT NULL DEFAULT 'THUONG',
  `kich_hoat` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `khach_hang`
--

INSERT INTO `khach_hang` (`khach_hang_id`, `ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `kich_hoat`) VALUES
(2, 'khach', '$2y$10$M3mk/O3ScQnZH3vZ0PZj0.1.94Fn41OSUsW.EbdPLWT3LGjB55MRS', 'Khách hàng mẫu', 'khach@example.com', 1),
(3, 'oxy', '$2a$10$65DVoHnlJtAiIDBQPz0iE.GzWCEssLxoFjqCWoW4WF3tdJIXR/aq.', 'lý kỳ lạc', 'lykylac2006@gmail.com', 1);

-- --------------------------------------------------------

--
-- Table structure for table `quan_tri`
--

CREATE TABLE `quan_tri` (
  `quan_tri_id` int(11) NOT NULL,
  `ten_dang_nhap` varchar(50) NOT NULL,
  `mat_khau` varchar(255) NOT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `kich_hoat` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `quan_tri`
--

INSERT INTO `quan_tri` (`quan_tri_id`, `ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `kich_hoat`) VALUES
(1, 'admin', '$2y$10$CDDaqFyHBn5hwrOLHzDkO.sU5CB/BOukRFWooL/Ph3PP74HphmFym', 'Quản trị viên', 'admin@techgear.vn', 1);

-- --------------------------------------------------------

--
-- Table structure for table `san_pham`
--

CREATE TABLE `san_pham` (
  `san_pham_id` int(11) NOT NULL,
  `phien_ban` bigint NOT NULL DEFAULT 0,
  `ten_san_pham` varchar(255) NOT NULL,
  `ma_san_pham` varchar(255) DEFAULT NULL,
  `thuong_hieu` varchar(255) DEFAULT NULL,
  `mo_ta` text DEFAULT NULL,
  `hinh_anh` varchar(255) DEFAULT NULL,
  `trang_thai` tinyint(1) DEFAULT 1,
  `noi_bat` tinyint(1) DEFAULT 0,
  `ban_chay` tinyint(1) DEFAULT 0,
  `gia_co_ban` decimal(15,0) DEFAULT NULL,
  `so_luong_ton` int(11) DEFAULT 0,
  `bao_hanh_thang` int(11) DEFAULT NULL,
  `danh_muc_id` int(11) DEFAULT NULL,
  `dat_truoc` bit(1) DEFAULT NULL,
  `ngay_du_kien` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `san_pham`
--

INSERT INTO `san_pham` (`san_pham_id`, `ten_san_pham`, `ma_san_pham`, `thuong_hieu`, `mo_ta`, `hinh_anh`, `trang_thai`, `noi_bat`, `ban_chay`, `gia_co_ban`, `so_luong_ton`, `bao_hanh_thang`, `danh_muc_id`, `dat_truoc`, `ngay_du_kien`) VALUES
(1, 'Zero IEM Headphones - Blue', 'TN001', 'TruthEar', '10 mm and 7.8 mm drivers.\r\nSensitivity: 117.5 dB/Vrms.\r\nIn-ear design, suitable for music listening.\r\nImpedance: 10Ω ± 15%.\r\nWide frequency range: 20 Hz - 39,500 Hz.', '7e0af823-3a9f-40de-9056-e45d34047a58.png', 1, 1, 1, 1890000, 15, 12, 1, NULL, NULL),
(2, 'Tai nghe Gaming không dây Razer BlackShark V3 Pro', 'TN002', 'Razer', 'Hybrid ANC: Chống ồn chủ động, chỉnh 4 mức.\r\n\r\nDriver Bio-Cellulose: TriForce 50mm Gen-2 chi tiết.\r\n\r\nHyperSpeed Gen-2: Không dây 2.4GHz, độ trễ 10ms.\r\n\r\nPin 70 giờ: Sạc nhanh 15 phút nghe 6 giờ.\r\n\r\nTHX Spatial Audio: Vòm 7.1.4 trên Windows 11.', '7c6921c6-a109-48a5-ad99-eb94b138ad2b.png', 1, 1, 0, 7000000, 40, 12, 1, NULL, NULL),
(3, 'HyperX Cloud II RED', 'TN003', 'HyperX', 'Driver 40mm, mang lại âm thanh sống động, cho trải nghiệm nghe nhạc và chơi game tuyệt vời.\r\nCông nghệ âm thanh không gian DTS:X Headphone, mang đến âm thanh vòm chân thực hơn.\r\nTrang bị nút điều khiển âm thanh dễ dàng tiếp cận, giúp người dùng tự điều chỉnh theo ý muốn.\r\nMicro tai nghe được thiết kế linh hoạt, cho phép người dùng điều chỉnh vị trí, giúp đàm thoại tốt.', '1f8953ac-9565-40ff-9c52-33cb325760b0.png', 1, 0, 1, 2400000, 18, 6, 1, NULL, NULL),
(4, 'Tai nghe Gaming chụp tai Sony Inzone H9', 'TN004', 'Sony', 'Âm thanh 360 Spatial Sound được cá nhân hóa dành riêng cho chơi game.\r\nThiết kế đệm đầu và đệm tai nghe êm ái, không gây bí bách khó chịu khi đeo.\r\nTrải nghiệm chơi game trọn vẹn hơn với khả năng khử mọi tạp âm hiệu quả.\r\nChiến mọi trận game không giới hạn với viên pin đến 32 giờ kèm sạc nhanh.', '275a386d-1764-4e99-8241-0a069e480419.png', 1, 0, 0, 3500000, 20, 12, 1, NULL, NULL),
(5, 'Tai nghe Logitech G Pro X Gaming Black', 'TN005', 'Logitech', 'Tai nghe Logitech G Pro X.\r\nTai nghe cao cấp nhất của Logitech.\r\nỨng dụng công nghệ tiên tiến nhất.\r\nTần số đáp ứng 20 - 20.000 Hz.\r\nMàng loa G-Pro 50mm.\r\nÁp dụng công nghệ DTS Headphone 2.0.', '67998256-7e54-4592-bfe5-d63e0d7d7efe.png', 1, 1, 0, 2500000, 22, 12, 1, NULL, NULL),
(6, 'Tai nghe Steelseries Arctis Nova Pro', 'TN006', 'SteelSeries', 'Drivers 40 mm.\r\nTính năng gameDAC gen 2 nâng tầm trải nghiệm âm thanh.\r\nTần số đáp ứng 20–22000 Hz.\r\nĐộ nhạy tai nghe 93 dBSPL.\r\nMicro Khử tiếng ồn ClearCast Gen 2.\r\nKết nối có dây, có thể kết nối 2 thiết bị cùng lúc.', '68c5bbd9-7de9-492d-ad53-aa1e63b93f37.png', 1, 1, 0, 6500000, 9, 12, 1, NULL, NULL),
(7, 'Củ sạc Ugreen RoboGaN Mini 30W USB-C CD359', 'SC001', 'Ugreen', 'Công nghệ GaN tiên tiến giúp củ sạc có kích thước nhỏ gọn mà vẫn đảm bảo công suất sạc cao đến 30W\r\nThiết kế độc đáo: Tạo điểm nhấn cho không gian làm việc hoặc học tập.\r\nSạc nhanh, hiệu quả: Tiết kiệm thời gian chờ đợi.\r\nĐược trang bị nhiều tính năng bảo vệ, bảo vệ thiết bị của bạn khỏi quá dòng, quá áp', 'f1544e52-c827-4493-8977-3c4177fe002b.png', 1, 1, 1, 400000, 40, 18, 2, NULL, NULL),
(8, 'Củ sạc Samsung Type-C 45W kèm cáp C-C 5A 1.8M EP-T4511X', 'SC002', 'Samsung', 'Công suất sạc 45W, sạc nhanh mọi thiết bị chỉ trong tích tắc, giúp bạn tiết kiệm thời gian\r\nDây cáp dài 1.8m, tiện lợi để sạc ở bất cứ đâu, cho phép bạn thoải mái sử dụng thiết bị trong khi sạc\r\nBảo vệ tối ưu chống quá dòng, đoản mạch và dao động nhiệt độ, đảm bảo an toàn cho thiết bị\r\nSạc nhanh cho nhiều loại thiết bị, từ tai nghe đến máy tính bảng, giúp bạn tiếp tục công việc dễ dàng\r\nSuper Fast Charging 2.0, hỗ trợ PD 3.0 và PDO/PPS với công suất tối đa 45W, cho hiệu suất sạc tối ưu', '1e1b8bb6-6947-4028-9ecf-2fca639c3d06.png', 1, 1, 0, 850000, 60, 12, 2, NULL, NULL),
(9, 'Cáp USB-C to Type-C Apple MW493ZA/A dây dù 1m chính hãng', 'SC003', 'Apple', 'Công suất sạc 60W, hỗ trợ sạc nhanh cho các thiết bị tương thích, tiết kiệm thời gian sạc\r\nChất liệu dây dù cao cấp giúp tăng độ bền, chống đứt gãy, đồng thời giảm tình trạng rối và gãy khi sử dụng\r\nTốc độ truyền dữ liệu USB 2.0, đảm bảo truyền tải dữ liệu nhanh chóng và hiệu quả giữa các thiết bị\r\nCáp tương thích với nhiều thiết bị của Apple, mang lại sự linh hoạt cho người dùng', 'c4c83c47-6d3b-4ecd-b7ed-e42f48a7df5a.png', 1, 0, 1, 540000, 45, 12, 2, NULL, NULL),
(10, 'Củ sạc Samsung 3 cổng 65W T6530', 'SC004', 'Samsung', 'Công suất 65W mạnh mẽ, sạc nhanh đồng thời nhiều thiết bị, lý tưởng cho người dùng đa nhiệm\r\n3 cổng đa dạng bao gồm 2 cổng Type-C và 1 cổng USB-A, tương thích với nhiều dòng máy khác nhau\r\nHỗ trợ Power Delivery và PPS, đảm bảo truyền năng lượng nhanh chóng cho cả laptop và thiết bị di động.\r\nChất liệu nhựa nhám, chống bám bụi và vân tay, giữ cho củ sạc luôn sạch sẽ và thẩm mỹ\r\nThiết kế nhỏ gọn và tinh tế, độ hoàn thiện cao, dễ dàng mang theo trong các chuyến đi', '43a1eda9-4974-42d4-b176-00a1ffade625.png', 1, 0, 0, 1000000, 30, 12, 2, NULL, NULL),
(11, 'Sạc Cuktech 6 Ultra 2C1A 100W PD 3.0 QC 4.0 màn hình AD653U kèm cáp type - C 240W 1M', 'SC005', 'Cuktech', 'Công suất tối đa 100W, sạc nhanh mạnh mẽ cho nhiều nhu cầu\r\nTích hợp 2 cổng USB-C và 1 cổng USB-A, dùng linh hoạt\r\nChuẩn sạc nhanh PD 3.0, QC 4.0 và PPS, tối ưu tốc độ sạc\r\nTự động nhận diện thiết bị, điều chỉnh phù hợp để an tâm hơn', '65159c23-24cf-4cf1-b12c-2dae2561a69e.png', 1, 1, 0, 490000, 28, 12, 2, NULL, NULL),
(12, 'Sạc Belkin BoostCharge Pro 70W 2C1A GaN PD3.1', 'SC006', 'Belkin', 'Sạc nhanh iPhone 16 Pro 0–50% trong 27 phút, Samsung S25 chỉ 26 phút.\r\nThiết kế siêu nhỏ gọn, dễ mang theo, lý tưởng cho mọi chuyến đi.\r\nCông nghệ GaN, PD, PPS sạc tối ưu, giảm nhiệt và tăng hiệu suất.\r\nAn toàn tuyệt đối với Belkin SmartProtect, thân thiện môi trường từ nhựa tái chế.', '96d6eb8d-c644-428a-a07f-bcdba0cae8c7.png', 1, 1, 1, 1000000, 4, 12, 2, NULL, NULL),
(13, 'Logitech G PRO X3 Superstrike ', 'CB001', 'Logitech', 'LOGITECH G PRO X3 SUPERSTRIKE nâng cấp Hệ thống kích hoạt cảm ứng rung phản hồi (HITS) với các linh kiện bên trong được tối ưu hóa và chipset hàng đầu mới. Cú nhấp chuột nhanh nhất, có thể tùy chỉnh toàn diện—được cải tiến hơn nữa.', 'e732e7cf-009c-46b6-80b7-e79a8bf653a5.png', 1, 1, 0, 4900000, 0, 24, 3, NULL, NULL),
(14, 'Bàn phím HE Wooting 60HE+', 'CB002', 'Wooting', 'Kích thước: 296.5 x 104 x 35 mm\r\nTrọng lượng: 725g\r\nChất liệu: nhựa (khung), PBT (phím)\r\nLoại switch: Lekker (cơ học, quang học)\r\nLực nhấn: 45g - 75g (có thể điều chỉnh)\r\nĐiểm phản hồi: 0.7mm - 3.6mm (có thể điều chỉnh)\r\nKeycap: PBT Doubleshot\r\nKết nối: USB Type-C\r\nPhần mềm: Wooting Wootility', '12a47b21-cadf-4834-9b7a-e4084ec13af1.png', 1, 1, 0, 5990000, 14, 12, 4, NULL, NULL),
(15, 'Chuột Razer Viper V4 Pro', 'CB003', 'Razer', 'Chuột Razer Viper V4 Pro.\r\nChuột gaming đối xứng cao cấp nhất của Razer.\r\nCảm biến quang Razer Focus Pro 50K.\r\nDPI tối đa lên tới 50.000.', '221e5674-caaa-4116-a3fa-8a181af2ba58.png', 1, 1, 1, 3790000, 20, 24, 3, NULL, NULL),
(16, 'Wooting 80HE - TenZ Takeover Limited Edition', 'CB004', 'Wooting', 'Bố cục: Bàn phím ANSI 80% dựa trên nền tảng Wooting 80HE.\r\nCông tắc: Công tắc Lekker Tikken TenZ edition nhẹ sử dụng hiệu ứng Hall.\r\nThiết kế: Vỏ nhựa ABS, tấm switch FR4, stabiliser bắt vít và keycap PBT nhuộm thăng hoa màu đen.\r\nHiệu năng: Rapid Trigger, hành trình kích hoạt có thể điều chỉnh và tần số polling thực 8000 Hz.\r\nPhiên bản giới hạn: 20.000 chiếc trên toàn cầu, trong đó 100 chiếc có chữ ký được Wooting phân phối ngẫu nhiên.', 'dd4625c8-77a1-472e-b249-07055f29b724.png', 1, 1, 0, 7940000, 10, 24, 4, NULL, NULL),
(17, 'Chuột Gaming ATK Blazing Sky X1 Ultra Black (Wired / Wireless 8K)', 'CB005', 'ATK', 'Cảm biến PAW3950 Ultra.\r\nReturn rate 8K.\r\nTrọng lượng 54g.\r\nKích thước 127 x 62 x40', '1343afb5-4306-4530-88ca-c6600d668c8c.png', 1, 0, 1, 1500000, 70, 24, 3, NULL, NULL),
(18, 'Bàn phím Razer Huntsman V3 Pro Mini', 'CB006', 'Razer', 'Kích thước/Layout:60%.\r\nPhương thức kết nối:Có dây.\r\nKiểu Switch:Switch quang học và từ tính.\r\nChất liệu vỏ:Nhựa, Kim Loại.\r\nTính năng đặc biệt:Rapid Trigger, Macro.\r\nTrọng lượng:550 g.', '59a649b3-c2eb-4f90-966d-4d7ed67a981a.png', 1, 1, 1, 3590000, 21, 24, 4, NULL, NULL),
(19, 'Bàn di chuột Pulsar PRX Edition Mousepad', 'PD001', 'Pulsar', 'Bản PRX Edition: Hợp tác cùng đội Paper Rex.\r\nCỡ XL: 490 x 420 mm, rộng rãi.\r\nVải dệt xử lý nhiệt: Lướt trung bình tới nhanh.\r\nViền may 360 độ: Chống tưa, phẳng với bề mặt.\r\nĐế cao su tự nhiên: Dày 3.5 mm, chống trượt.', 'ebc6b197-7e18-4f52-b460-d59bc25a2d5c.png', 1, 1, 0, 619000, 22, 6, 5, NULL, NULL),
(20, 'Bàn di chuột Pulsar PRX Edition Mousepad', 'PD002', 'Artisan', 'Loại bề mặt: Bề mặt vải dệt cân bằng (Balanced Textured Cloth Surface)\r\nĐặc tính: Khởi động nhanh, dừng chính xác (Easy to Start, Easy to Stop)\r\nChất liệu: 100% Polyester.', '53495bb1-5598-4d5e-bba9-227d1920d29e.png', 1, 1, 0, 1650000, 10, 6, 5, NULL, NULL),
(21, 'Bàn di chuột ARTISAN FX ZERO TenZ Red Edition', 'LB003', 'Artisan', 'Loại bề mặt	Balanced Textured Cloth\r\nChất liệu:Polyester 100%\r\nĐế:PORON Anti-slip\r\nĐộ cứng:SOFT\r\nĐộ dày:4mm\r\nMàu sắc:RED\r\nXuất xứ:Nhật Bản', '86310487-9095-4680-bca5-f5f5cdfc76dd.png', 1, 1, 0, 2390000, 6, 12, 5, NULL, NULL),
(22, 'Zowie G-SR-SE ROUGE II - Lót chuột chơi game eSports', 'PD004', 'Zowie', 'Bề mặt vải chống thấm và màu in với độ bền cao\r\nComfortGlide™: kiểm soát độ trượt ổn định\r\nĐế cao su có kết cấu đồng nhất, mật độ cao với độ mềm mại vừa phải\r\nKhông khâu viền cạnh, đế chống trượt\r\nKích thước: 470 x 390 mm', '6839c02a-d4f7-4457-bba6-c9adcba51b07.png', 1, 1, 0, 1390000, 7, 12, 5, NULL, NULL),
(23, 'Bàn Di Chuột Razer Gigantus V2 Pro - Large - Balance', 'PD005', 'Razer', 'Chất liệu bề mặt:Vải Balance\r\nChất liệu đế:GlideCore™ Foam - XSoft\r\nKích thước:500 x 480 x 4 mm\r\nTương thích feet:Feet PTFE,Feet kính', '6414aa39-7d81-42cc-b7df-adf78b871288.png', 1, 0, 0, 1199000, 19, 12, 5, NULL, NULL),
(24, 'Bàn di chuột Logitech G240 Cloth Gaming', 'PD006', 'Logitech', 'Kích thước cổ điển 280mm x 340mm.\r\nLót chuột vải mỏng 1mm chống trượt.\r\nBề mặt giảm ma sát giúp di chuyển mượt mà.\r\nCuộn lại dễ dàng, mang đi bất cứ đâu.', 'e4381407-f5ff-4a4f-b146-9c1abd6b9e8a.png', 1, 0, 1, 499000, 16, 18, 5, NULL, NULL),
(25, 'Pin sạc dự phòng Ugreen PB721 35524B 20000mAh 130W', 'DP001', 'Ugreen', 'Dung lượng pin:20000mAh\r\nCông suất sạc:130W\r\nTiện ích: Hỗ trợ các giao thức sạc UFC / PD / PPS / QC / SCP\r\nLõi pin:Lithium Ion\r\nKích thước:131 x 54 x 50.8 mm\r\nTrọng lượng:476 g', '802168b5-0976-4c2e-bfa2-0c9fda06352b.png', 1, 0, 1, 1400000, 15, 12, 6, NULL, NULL),
(26, 'Sạc Đôi Không Dây Samsung 15W', 'DP002', 'Samsung', 'Thiết kế:Sang trọng\r\nƯu điểm:Sạc nhanh, Sạc không dây\r\nKích thước:156 x 91 x 16.4 mm\r\nChất liệu:Nhựa cao cấp', '40e56ebe-b245-47c8-ada7-3b615d4e1382.png', 1, 0, 0, 499000, 8, 18, 6, NULL, NULL),
(27, 'Pin sạc dự phòng Baseus Enerfill FM21 Mini 10000mAh 45W', 'DP003', 'Baseus', 'Dung lượng pin:10000mAh\r\nCông suất sạc:45W\r\nTiện ích:Sạc nhanh, Sạc không dây\r\nKích thước:13 x 2.5 x 8.5cm\r\nTrọng lượng:221g', '5e7bbbe6-ade7-4445-9be6-5a5e27ffdb37.png', 1, 1, 0, 1090000, 13, 12, 6, NULL, NULL),
(28, 'Pin sạc dự phòng Anker Nano Maggo Ultra Slim A1665 Qi2 5000mAh', 'DP004', 'Anker', 'Dung lượng pin:5000 mAh\r\nCông suất sạc: Có dây 20W. Không dây 15W\r\nTiện ích:Đèn LED hiển thị dung lượng pin, Sạc nhiều thiết bị cùng lúc\r\nHiệu suất sạc:50%\r\nLõi pin:Polymer\r\nKích thước:122 g', '9b1d348e-328b-40b2-bb34-0e31fc407632.png', 1, 1, 0, 1290000, 11, 18, 6, NULL, NULL),
(31, 'Màn Hình Gaming BenQ ZOWIE XL2586X+ Fast TN 600Hz DyAc 2', 'MH001', 'Zowie', 'Kích thước : 25 inch\r\nTầm số quét : 600Hz\r\nĐộ phân giải : Full HD\r\nCông nghệ DyAc 2 mới cho game FPS\r\nTấm nền : Fast TN\r\nĐộ sáng (cd/㎡)‎: 320\r\nKết nối : HDMI 2.1 x3/ DP 1.4', '171d0018-3c1a-4f96-a0da-3b996a78a089.png', 1, 1, 0, 27850000, 12, 24, 7, NULL, NULL),
(32, 'Màn hình chơi game LG UltraGear™ OLED Chế độ kép 5K2K 45 inch ', 'MH002', 'LG', 'Màn hình OLED 5K2K 45 inch (5120x2160)\r\nDual-Mode (WUHD 165Hz ↔ WFHD 330Hz)\r\nThời gian phản hồi 0.03ms (GtG)\r\nDisplayPort 2.1 / USB-C (PD 90W)\r\nNVIDIA® G-SYNC® Compatible / VESA Certified AdaptiveSync / AMD FreeSync™ Premium Pro', 'caecb6f4-36b9-490c-bfeb-8f04eafa6463.png', 1, 1, 0, 59900000, 6, 24, 7, NULL, NULL),
(33, 'Màn hình BenQ MA270S 27 inch 5K 70Hz 5ms Nano Gloss IPS for MacBook', 'MH003', 'Zowie', 'Kích thước: 27 inch\r\nTần số quét: 70 Hz\r\nĐộ phân giải: 5120 × 2880 (5K)\r\nPhản hồi: 5 ms GtG\r\nĐộ phủ màu: 99% P3, 99% sRGB\r\nTấm nền: Nano Gloss IPS\r\nCổng kết nối: Thunderbolt 4, USB-C, HDMI', 'c18052b6-4a5a-41e7-9434-47e5faddad90.png', 1, 1, 0, 28490000, 8, 24, 7, NULL, NULL),
(34, 'Màn hình Gaming Samsung Odyssey G7 G70F LS27FG702EEXXV 27 inch', 'MH004', 'Samsung', 'Kích thước:27 inches\r\nTỉ lệ màn hình:16:9\r\nTần số quét:180 Hz\r\nThời gian phản hồi:1ms\r\nĐộ phân giải màn hình:3840 x 2160 pixels', '61359ff3-6830-4979-8df5-8bfd878d732b.png', 1, 0, 1, 13690000, 10, 24, 7, NULL, NULL),
(35, 'Màn Hình Gaming Asus XG27ACS-W 27 Inch/ WQHD/ IPS/ 180Hz/ 1ms/ Màu Trắng', 'MH005', 'Asus', 'Kiểu dáng màn hình: Phẳng\r\nTỉ lệ khung hình: 16:9\r\nKích thước mặc định: 27 inch\r\nCông nghệ tấm nền: Fast IPS\r\nPhân giải điểm ảnh: QHD (2560x1440)\r\nĐộ sáng hiển thị: 350cd/㎡\r\nTần số quét màn: 180Hz', 'c56bd67b-c47a-47ce-b7e5-9a90957e5b3a.png', 1, 0, 1, 6849000, 12, 24, 7, NULL, NULL),
(36, 'Màn hình Dell P2425H 24 inch', 'MH006', 'Dell', 'Kích thước thực tế:24 inches\r\nTỉ lệ màn hình:16:9\r\nTần số quét:100 Hz\r\nThời gian phản hồi:5ms\r\nTrọng lượng:3.1 kg\r\nĐộ phân giải màn hình:1920 x 1080 pixels', '976eac9c-82bc-4788-8160-7ee0c7d23cfa.png', 1, 0, 0, 4690000, 11, 24, 7, NULL, NULL),
(37, 'Pulsar TenZ Signature Edition – Chuột không dây giới hạn kèm dongle 8K', 'CB007', 'Pulsar', 'Hình dạng đối xứng.\r\nKhuyến nghị cho Claw Grip.\r\nSiêu nhẹ, chỉ 47g.\r\nHỗ trợ polling rate lên đến 8K.\r\nSwitch quang học siêu nhanh, không double-click.\r\nCảm biến XS-1 Flagship.\r\nTùy chỉnh hoàn toàn cài đặt cảm biến.\r\nTùy chỉnh phím và macro theo nhu cầu.\r\nĐiều chỉnh DPI theo đơn vị 10.\r\nCáp Superflex Paracord linh hoạt.\r\nThiết kế bởi TenZ và Pulsar Korea.', '4a6b4c82-8460-4d4b-a37e-afaaa1548077.png', 1, 0, 0, 3740000, 2, 24, 3, NULL, NULL),
(38, 'Chuột không dây Pulsar TenZ RED LIMITED EDITION', 'CB008', 'Pulsar', 'Thiết kế bởi TenZ.\r\nCảm biến Pulsar XS-1.\r\nTrọng lượng siêu nhẹ chỉ 47g.\r\nPolling lên đến 8000Hz.\r\nTối ưu cho Claw Grip.\r\nSwitch quang học.\r\nCông nghệ Motion Sync.\r\nĐiều chỉnh DPI linh hoạt.', '95d8ac93-2618-4ada-a31f-4ad0ab039c05.png', 1, 0, 0, 3949000, 0, 24, 3, NULL, NULL),
(39, 'Pulsar TenZ 2.0 – Chuột không dây kèm dongle 8K', 'CB009', 'Pulsar', 'Được thiết kế bởi TenZ: Tối ưu theo kiểu cầm chuột và phong cách thi đấu của TenZ.\r\nThiết kế đối xứng: Phù hợp kiểu cầm claw, cho kiểm soát nhanh và chính xác.\r\nTrọng lượng siêu nhẹ: 42g ±2g (Mini) / 47g ±2g (Medium)\r\nPolling Rate lên đến 8K: Tần số 8,000Hz, phản hồi nhanh và độ trễ thấp.\r\nSwitch quang học không double-click: Nhanh, ổn định và đáng tin cậy.\r\nCảm biến XS-2 thế hệ mới: 42,000 DPI, 800 IPS và 70G, cho tracking chính xác ở tốc độ cao.\r\nDPI 1 đơn vị & 11 mức LOD: Điều chỉnh DPI từng 1 đơn vị với X/Y độc lập và LOD qua 11 mức.\r\nTùy chỉnh phím & Macro: Tùy chỉnh chức năng nút bấm và Macro theo nhu cầu.\r\nLINK 2 Wireless Receiver: Kết nối không dây ổn định với độ trễ thấp.\r\nThiết kế bởi TenZ và Pulsar Korea.', 'ddddb797-df77-4473-bf3d-fe580da78728.png', 1, 1, 0, 3990000, 0, 24, NULL, NULL, NULL),
(40, 'Pulsar [Pro Series] BUZZ-X – Chuột eSports đối xứng', 'CB010', 'Pulsar', 'Trọng lượng siêu nhẹ: Thiết kế tối ưu hóa cấu trúc giúp chuột đạt trọng lượng siêu nhẹ (chỉ khoảng ~53g), thao tác vẩy chuột nhanh chóng mà không mỏi tay.\r\n\r\nCảm biến quang học PAW3395: Mang lại độ chính xác tuyệt đối với 26.000 DPI, 650 IPS và gia tốc 50G.\r\n\r\nSwitch quang học (Optical Switches): Loại bỏ hoàn toàn độ trễ debounce và lỗi double-click, tốc độ phản hồi chớp nhoáng.\r\n\r\nHỗ trợ Polling Rate 4K/8K Hz: Sẵn sàng cho trải nghiệm eSports chuyên nghiệp với độ trễ kết nối không dây gần như bằng 0 (khi dùng chung với Dongle 4K/8K).\r\n\r\nForm dáng Medium tối ưu: Thiết kế công thái học đối xứng, phù hợp hoàn hảo cho kiểu cầm Claw Grip và Fingertip Grip.', 'a5230d4e-59b1-4402-9457-147610b16b10.png', 1, 1, 0, 3580000, 2, 24, 3, NULL, NULL),
(41, 'Chuột Razer Deathadder V3 Pro Faker Edition', 'CB011', 'Razer', 'Màu sắc:Đỏ\r\nKết nối:Không dây\r\nThời lượng pin:Lên đến 90 giờ\r\nLed:Không\r\nCảm biến:Cảm biến quang học Focus Pro 30K\r\nDPI:30000\r\nIPS:750\r\nGia tốc tối đa (G):70\r\nSwitch:Optical Gen-3\r\nKích thước (D x R x C):128 x 68 x 44 mm\r\nTrọng lượng:63g', 'f4eea80d-9f1a-4ad5-a693-cb3889615bdb.png', 1, 1, 0, 3600000, 1, 24, 3, NULL, NULL),
(42, 'MelGeek MADE68 Ultra V2 – Bàn Phím Gaming AI 8000Hz Magnetic', 'Cb012', 'MelGeek', 'Switch:KOM Lite / Flip King Magnetic\r\nSwitch Travel:Adjustable 0.1mm – 3.4mm\r\nPolling Rate:8000Hz\r\nLatency:0.125ms\r\nRapid Trigger:0.01mm – 2.5mm\r\nVật liệu:Full Aluminum Alloy\r\nKết nối:Wired (USB)\r\nKích thước:319 × 117 × 40 mm\r\nTrọng lượng:~1175g', 'ef27cef3-facd-4bd3-a1d2-0c59d372175d.png', 1, 0, 1, 5890000, 16, 24, 4, NULL, NULL),
(43, 'Bàn phím HE Logitech PRO X TKL RAPID - Black', 'CB013', 'Logitech', 'Thương hiệu: Logitech .\r\n\r\nKiểu kết nối: có dây\r\nKết nối: USB-A sang USB-C.\r\nKiểu Switch: Analog\r\nMàu sắc: Đen\r\nKích thước: 357 x 150 x 38 mm.', '28076c1c-4e31-437a-a176-c3557652612d.png', 1, 1, 0, 3890000, 10, 24, 4, NULL, NULL),
(44, 'Bàn phím cơ Steelseries Apex Pro Mini', 'CB014', 'SteelSeries', 'Kết nối:Có dây USB-C có thể tháo rời\r\nSwitch:OmniPoint Adjustable Mechanical Switch\r\nBộ nhớ :5 profiles tuỳ chỉnh\r\nVi xử lý:32 Bit ARM\r\nkhoảng cách điểm nhận tín hiệu có thể tuỳ chỉnh:0.2- 3.8mm\r\nKích thước:293x103x40.3 mm\r\nTrọng lượng:610g\r\nLed:RGB', '9c411215-0bc8-4513-aa9e-9ff11a29c1e2.png', 1, 0, 0, 4490000, 9, 12, 4, NULL, NULL),
(45, 'Bàn phím HE Asus ROG Falchion Ace HFX Rapid Trigger', 'CB015', 'Asus', 'Key Switch:ROG HFX Magnetic Switch\r\nKết nối:USB 2.0 (Type-C to Type-A)\r\nĐèn LED:RGB từng phím\r\nPhím Macro:Tất cả các phím có thể lập trình\r\nTần số quét USB:8000 Hz\r\nKích thước:315 x 115 x 35 mm\r\nTrọng lượng:643g (không bao gồm cáp)\r\nMàu sắc:Đen', '4f0c80a0-f07a-4275-9730-dcaa696e7e3d.png', 1, 0, 0, 4690000, 12, 24, 4, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tai_khoan`
--

CREATE TABLE `tai_khoan` (
  `tai_khoan_id` int(11) NOT NULL,
  `ten_dang_nhap` varchar(50) NOT NULL,
  `mat_khau` varchar(255) NOT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `vai_tro` varchar(20) NOT NULL DEFAULT 'USER',
  `kich_hoat` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tai_khoan`
--

INSERT INTO `tai_khoan` (`tai_khoan_id`, `ten_dang_nhap`, `mat_khau`, `ho_ten`, `email`, `vai_tro`, `kich_hoat`) VALUES
(1, 'admin', '$2y$10$CDDaqFyHBn5hwrOLHzDkO.sU5CB/BOukRFWooL/Ph3PP74HphmFym', 'Quản trị viên', 'admin@techgear.vn', 'ADMIN', 1),
(2, 'khach', '$2y$10$M3mk/O3ScQnZH3vZ0PZj0.1.94Fn41OSUsW.EbdPLWT3LGjB55MRS', 'Khách hàng mẫu', 'khach@example.com', 'USER', 1),
(3, 'oxy', '$2a$10$65DVoHnlJtAiIDBQPz0iE.GzWCEssLxoFjqCWoW4WF3tdJIXR/aq.', 'lý kỳ lạc', 'lykylac2006@gmail.com', 'USER', 1);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_san_pham_dang_ban`
-- (See below for the actual view)
--
CREATE TABLE `v_san_pham_dang_ban` (
`san_pham_id` int(11)
,`ma_san_pham` varchar(255)
,`ten_san_pham` varchar(255)
,`thuong_hieu` varchar(255)
,`danh_muc_id` int(11)
,`ten_danh_muc` varchar(255)
,`gia_co_ban` decimal(15,0)
,`so_luong_ton` int(11)
,`bao_hanh_thang` int(11)
,`hinh_anh` varchar(255)
,`noi_bat` tinyint(1)
,`ban_chay` tinyint(1)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_san_pham_sap_het`
-- (See below for the actual view)
--
CREATE TABLE `v_san_pham_sap_het` (
`san_pham_id` int(11)
,`ma_san_pham` varchar(255)
,`ten_san_pham` varchar(255)
,`ten_danh_muc` varchar(255)
,`so_luong_ton` int(11)
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `binh_luan`
--
ALTER TABLE `binh_luan`
  ADD PRIMARY KEY (`binh_luan_id`),
  ADD KEY `FK9jg9a3tfpem1kh30nshlseyec` (`khach_hang_id`),
  ADD KEY `FKby76bvkd9v538sgerq6dcabp` (`san_pham_id`);

--
-- Indexes for table `chi_tiet_don_hang`
--
ALTER TABLE `chi_tiet_don_hang`
  ADD PRIMARY KEY (`chi_tiet_id`),
  ADD KEY `FKt57maavf6s28hxyar724mdr1b` (`don_hang_id`),
  ADD KEY `FKnlr5jpmfbvgad2w40cqo54mwg` (`san_pham_id`);

--
-- Indexes for table `danh_muc`
--
ALTER TABLE `danh_muc`
  ADD PRIMARY KEY (`danh_muc_id`);

--
-- Indexes for table `don_hang`
--
ALTER TABLE `don_hang`
  ADD PRIMARY KEY (`don_hang_id`),
  ADD KEY `FKeyporccvr9mq4k9j4fc5wpum5` (`khach_hang_id`);

--
-- Indexes for table `gio_hang`
--
ALTER TABLE `gio_hang`
  ADD PRIMARY KEY (`gio_hang_id`),
  ADD UNIQUE KEY `UKjk7p15beo16yf7wk851v1snwt` (`khach_hang_id`,`san_pham_id`),
  ADD KEY `FK39mx182qcf584c8jgdlstnwad` (`san_pham_id`);

--
-- Indexes for table `khach_hang`
--
ALTER TABLE `khach_hang`
  ADD PRIMARY KEY (`khach_hang_id`),
  ADD UNIQUE KEY `uk_khach_hang_ten_dang_nhap` (`ten_dang_nhap`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `quan_tri`
--
ALTER TABLE `quan_tri`
  ADD PRIMARY KEY (`quan_tri_id`),
  ADD UNIQUE KEY `uk_quan_tri_ten_dang_nhap` (`ten_dang_nhap`);

--
-- Indexes for table `san_pham`
--
ALTER TABLE `san_pham`
  ADD PRIMARY KEY (`san_pham_id`),
  ADD KEY `idx_sanpham_danhmuc` (`danh_muc_id`),
  ADD KEY `idx_sanpham_trangthai` (`trang_thai`),
  ADD KEY `idx_sanpham_ma` (`ma_san_pham`);

--
-- Indexes for table `tai_khoan`
--
ALTER TABLE `tai_khoan`
  ADD PRIMARY KEY (`tai_khoan_id`),
  ADD UNIQUE KEY `ten_dang_nhap` (`ten_dang_nhap`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `binh_luan`
--
ALTER TABLE `binh_luan`
  MODIFY `binh_luan_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `chi_tiet_don_hang`
--
ALTER TABLE `chi_tiet_don_hang`
  MODIFY `chi_tiet_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `danh_muc`
--
ALTER TABLE `danh_muc`
  MODIFY `danh_muc_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `don_hang`
--
ALTER TABLE `don_hang`
  MODIFY `don_hang_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `gio_hang`
--
ALTER TABLE `gio_hang`
  MODIFY `gio_hang_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `khach_hang`
--
ALTER TABLE `khach_hang`
  MODIFY `khach_hang_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `quan_tri`
--
ALTER TABLE `quan_tri`
  MODIFY `quan_tri_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `san_pham`
--
ALTER TABLE `san_pham`
  MODIFY `san_pham_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;

--
-- AUTO_INCREMENT for table `tai_khoan`
--
ALTER TABLE `tai_khoan`
  MODIFY `tai_khoan_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

-- --------------------------------------------------------

--
-- Structure for view `v_san_pham_dang_ban`
--
DROP TABLE IF EXISTS `v_san_pham_dang_ban`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_san_pham_dang_ban`  AS SELECT `sp`.`san_pham_id` AS `san_pham_id`, `sp`.`ma_san_pham` AS `ma_san_pham`, `sp`.`ten_san_pham` AS `ten_san_pham`, `sp`.`thuong_hieu` AS `thuong_hieu`, `dm`.`danh_muc_id` AS `danh_muc_id`, `dm`.`ten_danh_muc` AS `ten_danh_muc`, `sp`.`gia_co_ban` AS `gia_co_ban`, `sp`.`so_luong_ton` AS `so_luong_ton`, `sp`.`bao_hanh_thang` AS `bao_hanh_thang`, `sp`.`hinh_anh` AS `hinh_anh`, `sp`.`noi_bat` AS `noi_bat`, `sp`.`ban_chay` AS `ban_chay` FROM (`san_pham` `sp` left join `danh_muc` `dm` on(`dm`.`danh_muc_id` = `sp`.`danh_muc_id`)) WHERE `sp`.`trang_thai` = 1 ;

-- --------------------------------------------------------

--
-- Structure for view `v_san_pham_sap_het`
--
DROP TABLE IF EXISTS `v_san_pham_sap_het`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_san_pham_sap_het`  AS SELECT `sp`.`san_pham_id` AS `san_pham_id`, `sp`.`ma_san_pham` AS `ma_san_pham`, `sp`.`ten_san_pham` AS `ten_san_pham`, `dm`.`ten_danh_muc` AS `ten_danh_muc`, `sp`.`so_luong_ton` AS `so_luong_ton` FROM (`san_pham` `sp` left join `danh_muc` `dm` on(`dm`.`danh_muc_id` = `sp`.`danh_muc_id`)) WHERE `sp`.`so_luong_ton` <= 5 ORDER BY `sp`.`so_luong_ton` ASC ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `binh_luan`
--
ALTER TABLE `binh_luan`
  ADD CONSTRAINT `FK9jg9a3tfpem1kh30nshlseyec` FOREIGN KEY (`khach_hang_id`) REFERENCES `khach_hang` (`khach_hang_id`),
  ADD CONSTRAINT `FKby76bvkd9v538sgerq6dcabp` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`san_pham_id`);

--
-- Constraints for table `chi_tiet_don_hang`
--
ALTER TABLE `chi_tiet_don_hang`
  ADD CONSTRAINT `FKnlr5jpmfbvgad2w40cqo54mwg` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`san_pham_id`),
  ADD CONSTRAINT `FKt57maavf6s28hxyar724mdr1b` FOREIGN KEY (`don_hang_id`) REFERENCES `don_hang` (`don_hang_id`);

--
-- Constraints for table `don_hang`
--
ALTER TABLE `don_hang`
  ADD CONSTRAINT `FKeyporccvr9mq4k9j4fc5wpum5` FOREIGN KEY (`khach_hang_id`) REFERENCES `khach_hang` (`khach_hang_id`);

--
-- Constraints for table `gio_hang`
--
ALTER TABLE `gio_hang`
  ADD CONSTRAINT `FK39mx182qcf584c8jgdlstnwad` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`san_pham_id`),
  ADD CONSTRAINT `FKtfg3dplbmn3wiwy26si1daye3` FOREIGN KEY (`khach_hang_id`) REFERENCES `khach_hang` (`khach_hang_id`);

--
-- Constraints for table `san_pham`
--
ALTER TABLE `san_pham`
  ADD CONSTRAINT `fk_sanpham_danhmuc` FOREIGN KEY (`danh_muc_id`) REFERENCES `danh_muc` (`danh_muc_id`);
CREATE TABLE `khuyen_mai` (
  `khuyen_mai_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `san_pham_id` int NOT NULL,
  `ten` varchar(120) NOT NULL,
  `phan_tram` int NOT NULL,
  `tu_ngay` date NOT NULL,
  `den_ngay` date NOT NULL,
  KEY `idx_km_sp_ngay` (`san_pham_id`, `tu_ngay`, `den_ngay`),
  CONSTRAINT `fk_km_san_pham` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`san_pham_id`) ON DELETE CASCADE,
  CHECK (`phan_tram` BETWEEN 1 AND 100),
  CHECK (`tu_ngay` <= `den_ngay`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ma_giam_gia` (
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
CREATE TABLE `phieu_thanh_toan` (
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

CREATE TABLE `giao_hang` (
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

CREATE TABLE `nhat_ky_giao` (
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

CREATE TABLE IF NOT EXISTS `yeu_cau_doi_tra` (
  id bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  chi_tiet_id int NOT NULL,
  ma_gui varchar(36) NOT NULL UNIQUE,
  loai varchar(20) NOT NULL,
  buoc varchar(20) NOT NULL,
  so_luong int NOT NULL,
  ly_do varchar(500) NOT NULL,
  nguoi_tao varchar(255) NOT NULL,
  ngay_tao datetime(6) NOT NULL,
  ket_qua_kiem varchar(500) DEFAULT NULL,
  so_ban_lai int DEFAULT NULL,
  nguoi_kiem varchar(255) DEFAULT NULL,
  ngay_kiem datetime(6) DEFAULT NULL,
  ghi_chu_duyet varchar(500) DEFAULT NULL,
  nguoi_duyet varchar(255) DEFAULT NULL,
  ngay_duyet datetime(6) DEFAULT NULL,
  nguoi_xu_ly varchar(255) DEFAULT NULL,
  ngay_xu_ly datetime(6) DEFAULT NULL,
  gia_tri_tra decimal(15,0) DEFAULT NULL,
  phieu_hoan_id bigint DEFAULT NULL UNIQUE,
  san_pham_doi_id int DEFAULT NULL,
  KEY idx_doitra_dong (chi_tiet_id, id),
  KEY idx_doitra_buoc (buoc, id),
  FOREIGN KEY (chi_tiet_id) REFERENCES chi_tiet_don_hang(chi_tiet_id),
  FOREIGN KEY (phieu_hoan_id) REFERENCES phieu_thanh_toan(id),
  FOREIGN KEY (san_pham_doi_id) REFERENCES san_pham(san_pham_id),
  CHECK (loai IN ('TRA_HOAN_TIEN','DOI_SAN_PHAM')),
  CHECK (buoc IN ('TIEP_NHAN','DA_KIEM_TRA','DA_DUYET','TU_CHOI','HOAN_TAT')),
  CHECK (so_luong > 0 AND (so_ban_lai IS NULL OR so_ban_lai BETWEEN 0 AND so_luong)),
  CHECK (gia_tri_tra IS NULL OR gia_tri_tra >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

-- Báo cáo thống kê
ALTER TABLE san_pham ADD COLUMN gia_von decimal(15,0) DEFAULT NULL;
ALTER TABLE chi_tiet_don_hang ADD COLUMN gia_von_khi_ban decimal(15,0) DEFAULT NULL;
ALTER TABLE yeu_cau_doi_tra ADD COLUMN gia_von_hang_doi decimal(15,0) DEFAULT NULL;
ALTER TABLE don_hang ADD COLUMN nhan_vien_ban varchar(50) DEFAULT NULL;
ALTER TABLE don_hang ADD COLUMN ngay_hoan_thanh datetime(6) DEFAULT NULL;
CREATE INDEX idx_bc_giao ON don_hang (ngay_hoan_thanh);
CREATE INDEX idx_bc_dat ON don_hang (ngay_tao);
CREATE INDEX idx_bc_tra ON yeu_cau_doi_tra (ngay_xu_ly);
CREATE INDEX idx_bc_phieu ON phieu_thanh_toan (ngay_lap);
-- Chỉ lấy ngày giao từ sự kiện xác nhận thành công đã được ghi.
UPDATE don_hang d
JOIN (SELECT g.don_hang_id, MAX(n.thoi_diem) AS ngay
      FROM giao_hang g JOIN nhat_ky_giao n ON n.giao_hang_id = g.id
      WHERE n.su_kien = 'Xác nhận giao thành công'
      GROUP BY g.don_hang_id) cu ON cu.don_hang_id = d.don_hang_id
SET d.ngay_hoan_thanh = cu.ngay
WHERE d.trang_thai = 'HOAN_THANH' AND d.ngay_hoan_thanh IS NULL;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

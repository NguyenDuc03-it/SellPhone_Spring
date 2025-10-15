-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: sellphones
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `cart_item_id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `product_id` bigint NOT NULL,
  `cart_id` bigint NOT NULL,
  `rom` int DEFAULT NULL,
  PRIMARY KEY (`cart_item_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FKojy3ibx281qswho045bw4q0da` (`cart_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKojy3ibx281qswho045bw4q0da` FOREIGN KEY (`cart_id`) REFERENCES `shopping_carts` (`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (4,3,14,8,512),(6,2,5,5,256),(7,1,4,5,256),(8,5,9,5,256),(9,1,2,8,256),(10,1,15,8,256),(11,1,8,8,123),(12,1,10,8,512),(16,1,22,9,512),(17,1,12,7,512),(18,1,15,5,256),(20,1,39,37,256),(22,1,13,37,512),(23,1,38,37,256),(24,1,11,38,256),(25,1,22,38,512),(26,1,5,38,512),(27,2,1,38,128),(30,1,2,38,512),(31,1,34,38,1000),(32,1,30,15,256),(33,1,9,15,1000),(34,1,4,15,1000),(35,1,5,15,1000),(36,1,23,17,256),(37,1,35,17,512),(38,1,13,5,512),(43,1,40,39,256),(44,1,34,39,1000),(45,1,1,39,256),(46,1,9,1,256),(47,1,22,1,512),(48,1,15,1,256),(49,1,21,11,512),(51,5,29,5,128),(52,1,35,5,1000),(53,1,9,37,1000),(60,1,30,6,128);
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Apple','Các sản phẩm của Apple','Hoạt động'),(2,'SamSung','Các sản phẩm của SamSung','Hoạt động'),(3,'Oppo','Các sản phẩm của Oppo','Không hoạt động'),(4,'Xiaomi','Các sản phẩm của Xiaomi','Hoạt động'),(5,'Realme','Các sản phẩm của Realme','Hoạt động'),(7,'TECNO','TECNO','Hoạt động'),(12,'Honor','Các sản phẩm của Honor','Hoạt động'),(13,'Huawei','Các sản phẩm của Huawei','Hoạt động'),(14,'Nokia','Các sản phẩm của Nokia','Hoạt động'),(15,'Sony','Các sản phẩm của Sony Xperia','Hoạt động'),(19,'Vivo','Các sản phẩm của Vivo','Hoạt động'),(20,'OnePlus','Các sản phẩm của OnePlus','Hoạt động'),(21,'Google Pixel','Các sản phẩm của Google Pixel','Hoạt động');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` int NOT NULL,
  `rom` int DEFAULT NULL,
  `price` bigint NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,5,1,256,30290000),(2,2,5,1,512,37390000),(3,3,5,1,256,30290000),(4,4,2,1,256,4500000),(5,4,15,1,256,18900000),(6,5,10,1,512,17670000),(7,6,22,2,512,25900000),(8,7,13,1,512,24900000),(9,8,15,1,256,18900000),(10,9,11,2,512,32900000),(11,10,15,1,256,18900000),(12,11,11,1,256,29900000),(13,12,13,1,512,24900000),(14,13,37,1,256,10310000),(15,13,36,1,256,10310000),(16,14,34,1,1000,37990000),(17,15,11,1,512,29900000),(18,16,21,1,512,25900000),(19,17,15,1,256,18900000),(20,18,40,1,256,23500000),(23,21,NULL,1,123,14131311),(24,22,30,1,256,15990000),(25,23,30,1,512,25490000),(26,24,33,1,1000,50990000),(27,25,8,1,256,43990000),(28,26,29,1,512,24990000),(29,27,9,1,256,30290000),(30,28,23,1,256,22900000),(31,29,23,1,256,22900000),(32,30,23,1,256,22900000),(33,31,23,1,256,22900000),(34,32,23,1,256,22900000),(35,33,23,1,256,22900000),(36,34,22,1,512,25900000),(37,35,21,1,512,25900000),(38,36,5,1,256,30290000),(39,37,30,1,256,17990000),(40,37,22,1,512,25900000),(41,38,40,1,128,23500000);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `delivery_adress` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `order_time` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `delivery_time_end` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `order_status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `total_price` bigint NOT NULL,
  `payment_method` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,14,'ABC, XYZ','04/08/2025','11/09/2025','Đã hoàn thành',30290000,'Thanh toán khi nhận hàng'),(2,16,'ABC, XYZ','01/08/2025','08/08/2025','Đã hủy',37390000,'Thanh toán khi nhận hàng'),(3,6,'ABC, XYZ','15/09/2025','20/09/2025','Đã hủy',30290000,'Thanh toán khi nhận hàng'),(4,9,'ABC, XYZ','15/09/2025','20/09/2025','Đang xử lý',23400000,'Thanh toán khi nhận hàng'),(5,9,'ABC, XYZ','15/09/2025','20/09/2025','Chờ xử lý',17670000,'Thanh toán khi nhận hàng'),(6,8,'ABC, XYZ','15/09/2025','20/09/2025','Đã hủy',51800000,'Thanh toán khi nhận hàng'),(7,10,'Phố Nguyễn Thiếp, Kim Phú, Yên Sơn, Tuyên Quang, Việt Nam','15/09/2025','27/09/2025','Đã hoàn thành',24900000,'vnpay'),(8,10,'Phố Nguyễn Thiếp, Kim Phú, Yên Sơn, Tuyên Quang, Việt Nam','15/09/2025','27/09/2025','Đã hoàn thành',18900000,'vnpay'),(9,6,'ABC, XYZ','18/09/2025','27/09/2025','Đã hoàn thành',65800000,'vnpay'),(10,28,'Số 12 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh','27/09/2025','02/10/2025','Chờ xử lý',18900000,'Thanh toán khi nhận hàng'),(11,28,'Số 12 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh','27/09/2025','02/10/2025','Chờ xử lý',29900000,'Thanh toán khi nhận hàng'),(12,29,'abc','27/09/2025','02/10/2025','Chờ xử lý',24900000,'Thanh toán khi nhận hàng'),(13,29,'abc','27/09/2025','02/10/2025','Chờ thanh toán',20620000,'vnpay'),(14,11,'Mỹ Đình II, Nam Từ Liêm, Hà Nội.','27/09/2025','02/10/2025','Đang giao hàng',37990000,'vnpay'),(15,16,'ABC, XYZ','27/09/2025','02/10/2025','Chờ xử lý',29900000,'Thanh toán khi nhận hàng'),(16,16,'ABC, XYZ','27/09/2025','05/10/2025','Đã hoàn thành',25900000,'Thanh toán khi nhận hàng'),(17,18,'ABC, XYZ','27/09/2025','02/10/2025','Đang giao hàng',18900000,'Thanh toán khi nhận hàng'),(18,18,'ABC, XYZ','27/09/2025','02/10/2025','Chờ xử lý',23500000,'Thanh toán khi nhận hàng'),(21,6,'ABC, XYZ','28/09/2025','28/09/2025','Đã hoàn thành',14131311,'Thanh toán khi nhận hàng'),(22,31,'ABC, XYZ','04/10/2025','09/10/2025','Đã hủy',15990000,'Thanh toán khi nhận hàng'),(23,31,'ABC, XYZ','04/10/2025','05/10/2025','Đã hoàn thành',25490000,'Thanh toán khi nhận hàng'),(24,25,'ABC,XYZ','04/10/2025','05/10/2025','Đã hoàn thành',50990000,'vnpay'),(25,12,'Số 8, Nguyễn Cơ Thạch, Nam Từ Liêm, Hà Nội','04/10/2025','05/10/2025','Đã hoàn thành',43990000,'Thanh toán khi nhận hàng'),(26,31,'ABC, XYZ','05/10/2025','10/10/2025','Chờ xử lý',24990000,'Thanh toán khi nhận hàng'),(27,26,'abc, xyz','07/10/2025','08/10/2025','Đã hoàn thành',30290000,'Thanh toán khi nhận hàng'),(28,26,'abc, xyz','07/10/2025','12/10/2025','Đã hủy',22900000,'vnpay'),(29,26,'abc, xyz','07/10/2025','12/10/2025','Đã hủy',22900000,'vnpay'),(30,26,'abc, xyz','07/10/2025','12/10/2025','Đã hủy',22900000,'vnpay'),(31,26,'abc, xyz','07/10/2025','12/10/2025','Đã hủy',22900000,'vnpay'),(32,26,'abc, xyz','07/10/2025','12/10/2025','Đã hủy',22900000,'Thanh toán khi nhận hàng'),(33,26,'abc, xyz','07/10/2025','12/10/2025','Chờ thanh toán',22900000,'vnpay'),(34,8,'ABC, XYZ','08/10/2025','08/10/2025','Đã hoàn thành',25900000,'Thanh toán khi nhận hàng'),(35,8,'ABC, XYZ','08/10/2025','08/10/2025','Đã hoàn thành',25900000,'Thanh toán khi nhận hàng'),(36,27,'Số 8 Tràng Thi, Phường Hàng Trống, Quận Hoàn Kiếm, Hà Nội','08/10/2025','13/10/2025','Chờ xử lý',30290000,'Thanh toán khi nhận hàng'),(37,28,'Số 12 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh','08/10/2025','13/10/2025','Chờ xử lý',43890000,'Thanh toán khi nhận hàng'),(38,7,'ABC, XYZ','08/10/2025','13/10/2025','Chờ xử lý',23500000,'Thanh toán khi nhận hàng');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `category_id` bigint NOT NULL,
  `color` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `specification_id` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `category_id` (`category_id`),
  KEY `FKtqc5tuap57xpjobodjt3x2jaj` (`specification_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `FKtqc5tuap57xpjobodjt3x2jaj` FOREIGN KEY (`specification_id`) REFERENCES `specifications` (`specification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Samsung Galaxy A56 5G 8GB 128GB','/uploads/products/12217ebe-df13-46fd-a99d-17e03ece0ffc_samsung-galaxy-a56.png',2,'Đen','Còn hàng','Điện thoại Samsung A56 5G sở hữu màn hình Super AMOLED 6,7 inch độ phân giải FHD+ và tần số quét 120 Hz, đem tới trải nghiệm xem sắc nét và mượt mà. Thiết bị trang bị chip Exynos 1580 tiến trình 4nm, giúp xử lý đa nhiệm nhanh chóng, tối ưu hiệu năng. Viên pin 5000mAh hỗ trợ sạc nhanh 45W cung cấp cho máy thời lượng sử dụng cả ngày dài mà không lo gián đoạn.',2),(2,'OPPO Find N5','/uploads/products/4416f3f7-5f70-498d-ac5e-b0f43b2354a8_oppo_find_N5.png',3,'Đen','Còn hàng','OPPO Find N5 được trang bị CPU Snapdragon 8 Elite mạnh nhất trong dòng điện thoại gập, tích hợp AI đến từ Qualcom cho hiệu năng vận hành mạnh mẽ mà vẫn tối ưu năng lượng tiêu hao. Sản phẩm sở hữu viên pin Silicone-carbon mỏng nhất thế giới, mang lại dung lượng cao 5600mAh. Cụm camera đảm bảo chất lượng ảnh, video được quay chụp sắc nét với màu sắc chân thực.',4),(3,'Samsung Galaxy S25 256GB','/uploads/products/b7e0a6bf-a58b-4b3c-82b7-27abe0e7a664_samsung-s25.png',2,'Xanh lá','Còn hàng','Samsung S25 thường 256GB trang bị vi xử lý Qualcomm Snapdragon 8 Elite for Galaxy , RAM 12GB, bộ nhớ trong 256GB cùng viên pin Li-ion 4000mAh. Máy sử dụng màn hình AMOLED 120Hz kích thước 6.2 inch với độ phân giải FHD+. Samsung S25 được trang bị 3 ống kính 50MP+10MP+12MP phía sau và camera selfie 12MP phía trước.',5),(4,'iPhone 16 Pro Max 256GB | Chính hãng VN/A','/uploads/products/3994e316-88cf-47fc-bbad-6035713feaab_iphone-16-pro-max-titan-trang.png',1,'Titan Trắng ','Còn hàng','iPhone 16 Pro Max là phiên bản di động cao cấp nhất của Apple năm 2024 với nhiều cải tiến chưa từng có.',6),(5,'iPhone 16 Pro Max 256GB | Chính hãng VN/A','/uploads/products/fa6c2e22-aa8b-4453-bf41-a2644b1f9b5a_iphone-16-pro-max-titan-den.png',1,'Titan Đen','Còn hàng','iPhone 16 Pro Max là phiên bản di động cao cấp nhất của Apple năm 2024 với nhiều cải tiến chưa từng có.',8),(8,'Samsung Galaxy Z Fold7 12GB','/uploads/products/c16ba31a-72ae-4260-affa-a0e4d7a54acb_samsung-galaxy-z-fold-7-xanh_1_1.png',2,'Xanh bóng','Còn hàng','Samsung Galaxy Z Fold 7 512GB đã ra mắt, mang đến \"Trải nghiệm Ultra. Gập mở\" đầy ấn tượng. Nổi bật nhờ thiết kế mỏng nhẹ, hiệu năng mạnh mẽ, camera chuẩn Ultra và AI thông minh vượt trội. Z Fold 7 khẳng định vị thế dẫn đầu, mở ra kỷ nguyên mới cho trải nghiệm gập của bạn.',11),(9,'iPhone 16 Pro Max 256GB | Chính hãng VN/A','/uploads/products/599fb129-2840-42a5-a51c-e9971ce47f31_iphone-16-pro-max-titan-tu-nhien_1.png',1,'Titan Tự Nhiên','Còn hàng','iPhone 16 Pro Max là phiên bản di động cao cấp nhất của Apple năm 2024 với nhiều cải tiến chưa từng có.',12),(10,'Xiaomi 14T Pro 12GB 512GB','/uploads/products/5ee50839-1810-4145-b39f-7c4eb2c50aa4_234_11_.png',4,'Xám','Còn hàng','Xiaomi 14T Pro 5G là một trong những mẫu smartphone cao cấp được mong đợi. Được đánh giá mang nhiều cải tiến đáng giá, đặc biệt là trong hiệu năng và thiết kế của sản phẩm.',13),(11,'iPhone 17 Pro Max 256GB','/uploads/products/a09ac01a-2d79-4465-a85a-68e28435f18c_600_iphone-17-pro-max-256gb.png',1,'Titan Xám','Còn hàng','iPhone 17 Pro Max cao cấp mới nhất.',14),(12,'Samsung Galaxy S27 Ultra 512GB','/uploads/products/8338f098-76df-4852-b15b-5f5ec9730b2b_galaxy-s27-ultra-1.png',2,'Xám','Còn hàng','Galaxy S27 Ultra mạnh mẽ với camera 200MP.',15),(13,'Oppo Find X8 Pro 512GB','/uploads/products/0126f605-0809-40eb-9a9d-60fb8bdee010_product_find_x8_grey.png',3,'Xám','Còn hàng','Flagship Oppo với camera zoom 64MP.',16),(14,'Xiaomi 15 Ultra 512GB','/uploads/products/80c3c35f-66ea-47af-9f9e-a698afcd38b1_xiaomi_15_ultra.png',4,'Bạc','Còn hàng','Xiaomi 15 Ultra hợp tác Leica.',17),(15,'Realme GT Neo 7 256GB','/uploads/products/65ea9d0d-b939-44aa-b132-f5b669e6bd0f_realme-neo7-trang.jpg.png',5,'Trắng','Còn hàng','Realme GT Neo 7 hiệu năng mạnh mẽ giá tốt.',18),(21,'Vivo X200 Pro+ 512GB','/uploads/products/969a9056-78ab-4143-a0b5-1e853ab7519b_vivo_x200_pro_titanium.png',19,'Titanium','Còn hàng','Vivo X200 Pro+ camera ZEISS.',25),(22,'OnePlus 13 512GB','/uploads/products/197d899f-8745-44e9-b96a-a95ebe52abab_oneplus-13-xanh.jpg.png',20,'Xanh','Còn hàng','OnePlus 13 tốc độ, OxygenOS mượt.',26),(23,'Google Pixel 9 256GB','/uploads/products/2e46afa9-51bb-4ec7-aadb-2c50d7aaa83c_600_google_pixel_9_porcelain.png',21,'Porcelain','Còn hàng','Pixel 9 AI tối ưu.',27),(29,'iPhone 16e | Chính hãng VN/A','/uploads/products/08a64079-bdfb-419e-8005-85e46bd63e5c_iphone-16e-512gb.png',1,'Trắng','Còn hàng','ĐIện thoại iPhone 16e 256GB được trang bị màn hình Super Retina XDR 6.1 inch, với bộ vi xử lý A18 mạnh mẽ, và hệ thống camera camera 2 trong 1 với ống kính 48MP. Thiết bị cũng hỗ trợ 5G, với dung lượng lên tới 256GB, cùng pin cải thiện thời gian sử dụng. Sản phẩm có thiết kế mỏng nhẹ, khả năng chống nước IP68, và sạc nhanh 20W, tính tiện dụng của máy được nâng cao.',35),(30,'iPhone 16e | Chính hãng VN/A','/uploads/products/58f47bb9-4dcb-4552-b952-dc4a4b67f266_iphone-16e-128gb_den.png',1,'Đen','Còn hàng','ĐIện thoại iPhone 16e 256GB được trang bị màn hình Super Retina XDR 6.1 inch, với bộ vi xử lý A18 mạnh mẽ, và hệ thống camera camera 2 trong 1 với ống kính 48MP. Thiết bị cũng hỗ trợ 5G, với dung lượng lên tới 256GB, cùng pin cải thiện thời gian sử dụng. Sản phẩm có thiết kế mỏng nhẹ, khả năng chống nước IP68, và sạc nhanh 20W, tính tiện dụng của máy được nâng cao.',36),(31,'Samsung Galaxy Z Fold7 12GB','/uploads/products/8deae460-4854-43dd-b339-4fd29596cf74_samsung-galaxy-z-fold-7-xam_bong.png',2,'Xám bóng','Còn hàng','Samsung Galaxy Z Fold 7 512GB đã ra mắt, mang đến \"Trải nghiệm Ultra. Gập mở\" đầy ấn tượng. Nổi bật nhờ thiết kế mỏng nhẹ, hiệu năng mạnh mẽ, camera chuẩn Ultra và AI thông minh vượt trội. Z Fold 7 khẳng định vị thế dẫn đầu, mở ra kỷ nguyên mới cho trải nghiệm gập của bạn.',37),(32,'Samsung Galaxy Z Fold7 12GB','/uploads/products/4df57bac-3259-43c1-aa54-a29a30771fb6_samsung-galaxy-z-fold-7-den_tuyen.png',2,'Đen tuyền','Còn hàng','Samsung Galaxy Z Fold 7 512GB đã ra mắt, mang đến \"Trải nghiệm Ultra. Gập mở\" đầy ấn tượng. Nổi bật nhờ thiết kế mỏng nhẹ, hiệu năng mạnh mẽ, camera chuẩn Ultra và AI thông minh vượt trội. Z Fold 7 khẳng định vị thế dẫn đầu, mở ra kỷ nguyên mới cho trải nghiệm gập của bạn.',38),(33,'iPhone 17 Pro Max','/uploads/products/7c22cb11-e7c7-40e9-a79e-18f535326337_iphone-17-pro-xanh_dam.png',1,'Xanh đậm','Còn hàng','iPhone 17 Pro Max 1TB là người bạn đồng hành lý tưởng cho mọi hoạt động của bạn. Đây là một bản nâng cấp toàn diện, từ thiết kế bên ngoài đến sức mạnh bên trong. Bên cạnh đó, với dung lượng khổng lồ lên tới 1TB, bạn có thể thoải mái lưu lại hàng ngàn bức ảnh, video, ứng dụng và tài liệu quan trọng mà không lo đầy bộ nhớ.',39),(34,'iPhone 17 Pro Max','/uploads/products/8ba8d55d-51b4-4c9c-ada0-aca34098462a_iphone-17-pro-cam_vu_tru.png',1,'Cam vũ trụ','Còn hàng','iPhone 17 Pro Max 1TB là người bạn đồng hành lý tưởng cho mọi hoạt động của bạn. Đây là một bản nâng cấp toàn diện, từ thiết kế bên ngoài đến sức mạnh bên trong. Bên cạnh đó, với dung lượng khổng lồ lên tới 1TB, bạn có thể thoải mái lưu lại hàng ngàn bức ảnh, video, ứng dụng và tài liệu quan trọng mà không lo đầy bộ nhớ.',40),(35,'iPhone 17 Pro Max','/uploads/products/1f2a66dc-3ec1-42eb-93ef-ed48269cf687_iphone-17-pro-3_bac.png',1,'Bạc','Ngừng bán','iPhone 17 Pro Max 1TB là người bạn đồng hành lý tưởng cho mọi hoạt động của bạn. Đây là một bản nâng cấp toàn diện, từ thiết kế bên ngoài đến sức mạnh bên trong. Bên cạnh đó, với dung lượng khổng lồ lên tới 1TB, bạn có thể thoải mái lưu lại hàng ngàn bức ảnh, video, ứng dụng và tài liệu quan trọng mà không lo đầy bộ nhớ.',41),(36,'Vivo V50 LITE 5G 12GB 256GB','/uploads/products/58ee1cea-0082-41c7-bd06-dc190587e819_vivo-v50-lite-5g_den_tim.png',19,'Đen tím','Còn hàng','Vivo V50 Lite 5G nổi bật với chip xử lý MediaTek Dimensity 6300 đi cùng chuẩn RAM LPDDR4X 12GB, cộng thêm dung lượng 256GB UFS 2.2 của bộ nhớ trong. Thêm vào đó, màn hình AMOLED của mẫu điện thoại vivo V này còn đạt độ sáng lên đến 1800 nits. Máy được trang bị dung lượng pin cao 6500 mAh kèm sạc nhanh 90W cũng đem lại sự tiện lợi khi sử dụng.',42),(37,'Vivo V50 LITE 5G 12GB 256GB','/uploads/products/b78d7164-348c-4e0e-8138-228ac2e13f52_vivo-v50-lite-5g_vang.png',19,'Vàng','Còn hàng','Vivo V50 Lite 5G nổi bật với chip xử lý MediaTek Dimensity 6300 đi cùng chuẩn RAM LPDDR4X 12GB, cộng thêm dung lượng 256GB UFS 2.2 của bộ nhớ trong. Thêm vào đó, màn hình AMOLED của mẫu điện thoại vivo V này còn đạt độ sáng lên đến 1800 nits. Máy được trang bị dung lượng pin cao 6500 mAh kèm sạc nhanh 90W cũng đem lại sự tiện lợi khi sử dụng.',43),(38,'Vivo V30E 12GB 256GB','/uploads/products/e4e07974-3184-432a-9d69-d0960445106a_dien-thoai-vivo-v30.png',19,'Trắng','Còn hàng','Điện thoại vivo V30E mạnh mẽ với chipset Snapdragon 6 Gen 1, kết hợp với 12GB RAM và 256GB bộ nhớ trong, đảm bảo hiệu suất ấn tượng cho mọi tác vụ. Màn hình AMOLED 6.68 inch của máy cung cấp trải nghiệm hình ảnh sắc nét đi kèm cụm camera kép ở mặt sau với camera chính lên đến 50MP. Vivo V30E còn sở hữu pin dung lượng 5.500 mAh, hỗ trợ sạc nhanh 44W, tối ưu hóa trải nghiệm sử dụng hàng ngày.',44),(39,'Google Pixel 10','/uploads/products/62b7d215-6372-46b9-bfd3-b7187e7891fe_pixel-10-lemoncello.png',21,'Lemoncello','Còn hàng','Google Pixel 10 đã chính thức ra mắt với 4 phiên bản màu sắc (Đen, Màu xanh da trời, Màu xanh lá, màu tím). Tuy “ngoại hình” không quá khác biệt so với Pixel 9, nhưng Google đã đem đến cho người dùng rất nhiều nâng cấp đáng giá!',45),(40,'Google Pixel 10','/uploads/products/832cbd0e-a285-4999-840a-92b09dbebca5_pixel-10_frost.png',21,'Frost','Còn hàng','Google Pixel 10 đã chính thức ra mắt với 4 phiên bản màu sắc (Đen, Màu xanh da trời, Màu xanh lá, màu tím). Tuy “ngoại hình” không quá khác biệt so với Pixel 9, nhưng Google đã đem đến cho người dùng rất nhiều nâng cấp đáng giá!',46);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopping_carts`
--

DROP TABLE IF EXISTS `shopping_carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_carts` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK3iw2988ea60alsp0gnvvyt744` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopping_carts`
--

LOCK TABLES `shopping_carts` WRITE;
/*!40000 ALTER TABLE `shopping_carts` DISABLE KEYS */;
INSERT INTO `shopping_carts` VALUES (2,1),(3,3),(4,4),(34,5),(5,6),(6,7),(7,8),(8,9),(9,10),(10,11),(11,12),(12,13),(13,14),(14,15),(15,16),(16,17),(17,18),(18,24),(1,25),(35,26),(36,27),(37,28),(38,29),(39,31);
/*!40000 ALTER TABLE `shopping_carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specification_variants`
--

DROP TABLE IF EXISTS `specification_variants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specification_variants` (
  `specification_variant_id` bigint NOT NULL AUTO_INCREMENT,
  `specification_id` bigint NOT NULL,
  `rom` int DEFAULT NULL,
  `import_price` bigint DEFAULT NULL,
  `selling_price` bigint DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `import_date` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`specification_variant_id`),
  UNIQUE KEY `UKa089jr49y083yqrxknyt006dv` (`specification_id`,`rom`),
  CONSTRAINT `FKtehgee6sl9wu5lkwx04glskyo` FOREIGN KEY (`specification_id`) REFERENCES `specifications` (`specification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=221 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specification_variants`
--

LOCK TABLES `specification_variants` WRITE;
/*!40000 ALTER TABLE `specification_variants` DISABLE KEYS */;
INSERT INTO `specification_variants` VALUES (5,3,31,231,313,1,'25/07/2025'),(6,3,12,231,123,2,'25/07/2025'),(82,13,512,12500000,17670000,9,'26/08/2025'),(116,15,512,30000000,36900000,10,'01/09/2025'),(117,15,1024,35000000,41900000,5,'01/09/2025'),(118,16,512,20000000,24900000,10,'01/09/2025'),(119,17,512,22000000,27900000,8,'01/09/2025'),(120,18,256,15000000,18900000,22,'01/09/2025'),(121,25,512,21000000,25900000,8,'01/09/2025'),(122,26,512,21000000,25900000,8,'01/09/2025'),(123,27,256,18000000,22900000,17,'01/09/2025'),(130,14,256,25000000,29900000,19,'08/09/2025'),(131,14,512,28000000,32900000,16,'08/09/2025'),(136,8,256,29000000,30290000,15,'08/09/2025'),(137,8,512,33000000,37390000,11,'08/09/2025'),(138,8,1000,38000000,42690000,7,'08/09/2025'),(148,12,256,29000000,30290000,9,'12/09/2025'),(149,12,512,33000000,37590000,8,'12/09/2025'),(150,12,1000,39000000,42990000,5,'12/09/2025'),(151,6,256,29290000,31250000,5,'12/09/2025'),(152,6,512,34120000,36990000,5,'12/09/2025'),(153,6,1000,39590000,42990000,5,'12/09/2025'),(157,2,128,9010000,9510000,10,'23/09/2025'),(158,2,256,10050000,10980000,10,'23/09/2025'),(162,35,128,14710000,15590000,10,'23/09/2025'),(163,35,256,16550000,17990000,10,'23/09/2025'),(164,35,512,23000000,24990000,9,'23/09/2025'),(165,36,512,24410000,25490000,9,'23/09/2025'),(166,36,256,16550000,17990000,9,'23/09/2025'),(167,36,128,14900000,15990000,10,'23/09/2025'),(168,11,256,41500000,43990000,9,'23/09/2025'),(169,11,512,45990000,47990000,10,'23/09/2025'),(170,37,256,41500000,43990000,10,'23/09/2025'),(171,37,512,45990000,47990000,10,'23/09/2025'),(172,38,256,41500000,43990000,10,'23/09/2025'),(173,38,512,45990000,47990000,10,'23/09/2025'),(174,5,256,15400000,17690000,10,'23/09/2025'),(187,42,256,9570000,10310000,10,'24/09/2025'),(188,43,256,9570000,10310000,10,'24/09/2025'),(189,44,256,7100000,8490000,15,'24/09/2025'),(190,45,128,21100000,23500000,8,'24/09/2025'),(191,45,256,24130000,26500000,6,'24/09/2025'),(196,4,512,41150000,44180000,10,'24/09/2025'),(201,41,256,34510000,37990000,10,'29/09/2025'),(202,41,512,42010000,44490000,10,'29/09/2025'),(203,41,1000,48700000,50990000,10,'29/09/2025'),(204,41,2000,60510000,63990000,10,'29/09/2025'),(205,40,256,34510000,37990000,10,'29/09/2025'),(206,40,512,42010000,44490000,10,'29/09/2025'),(207,40,1000,48700000,50990000,9,'29/09/2025'),(208,40,2000,60510000,63990000,10,'29/09/2025'),(215,46,128,21100000,23500000,8,'08/10/2025'),(216,46,256,24130000,26500000,7,'08/10/2025'),(217,39,256,34510000,37990000,10,'08/10/2025'),(218,39,512,42010000,44490000,10,'08/10/2025'),(219,39,1000,48700000,50990000,9,'08/10/2025'),(220,39,2000,60510000,63990000,10,'08/10/2025');
/*!40000 ALTER TABLE `specification_variants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specifications`
--

DROP TABLE IF EXISTS `specifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specifications` (
  `specification_id` bigint NOT NULL AUTO_INCREMENT,
  `screen_size` float NOT NULL,
  `rear_camera` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `front_camera` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `chipset` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ram` int NOT NULL,
  `sim` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `operating_system` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cpu` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `charging` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`specification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specifications`
--

LOCK TABLES `specifications` WRITE;
/*!40000 ALTER TABLE `specifications` DISABLE KEYS */;
INSERT INTO `specifications` VALUES (2,6.7,'50.0 MP, F/1.8 + 12.0 MP, F/2.2 + 5.0 MP, F/2.4','12.0 MP, F/2.2','Exynos 1580',8,'SIM 1 + SIM 2 / SIM 1 + eSIM / 2 eSIM','Android','8 nhân 2.9GHz, 2.6GHz, 1.9GHz','USB Type-C'),(3,21,'131x','213s','13s',4,'213xa','ax2','23x','231f'),(4,8.12,'50MP, ƒ/1.8 OIS (Chính) + 8MP, ƒ/2.2 (Góc rộng) + 50MP, ƒ/2.7 (Tele)','Màn hình chính: 8MP, ƒ/2.4/ \r\nMàn hình ngoài: 8MP, ƒ/2.4','Qualcomm Snapdragon® 8 Elite',16,'Dual nano-SIM hoặc 1 nano-SIM + 1 eSIM','ColorOS 15','Snapdragon 8 Elite','Siêu sạc nhanh superVOOC 80W'),(5,6.2,'Camera siêu rộng 12MP\r\nCamera góc rộng 50MP\r\nCamera Tele 10MP','12MP','Snapdragon 8 Elite dành cho Galaxy (3nm)',12,'2 Nano SIM hoặc 2 eSIM hoặc 1 Nano SIM + 1 eSIM','Android 15','Tốc độ CPU 4.47GHz, 3.5GHz, \r\n8 nhân','Sạc nhanh 25W, 	\r\nUSB Type-C'),(6,6.9,'Camera chính: 48MP, f/1.78, 24mm, 2µm, chống rung quang học dịch chuyển cảm biến thế hệ thứ hai, Focus Pixels 100%\r\nTelephoto 2x 12MP: 52 mm, ƒ/1.6\r\nCamera góc siêu rộng: 48MP, 13 mm,ƒ/2.2 và trường ảnh 120°, Hybrid Focus Pixels, ảnh có độ phân giải','12MP, ƒ/1.9, Tự động lấy nét theo pha Focus Pixels','Apple A18 Pro',8,'Sim kép (nano-Sim và e-Sim) - Hỗ trợ 2 e-Sim','iOS 18','6 lõi mới với 2 lõi hiệu năng và 4 lõi hiệu suất','Sạc không dây MagSafe lên đến 25W với bộ tiếp hợp 30W trở lên\r\nSạc không dây Qi2 lên đến 15W\r\nSạc không dây Qi lên đến 7,5W'),(8,6.9,'Camera chính: 48MP, f/1.78, 24mm, 2µm, chống rung quang học dịch chuyển cảm biến thế hệ thứ hai, Focus Pixels 100%\r\nTelephoto 2x 12MP: 52 mm, ƒ/1.6\r\nCamera góc siêu rộng: 48MP, 13 mm,ƒ/2.2 và trường ảnh 120°, Hybrid Focus Pixels, ảnh có độ phân giải','12MP, ƒ/1.9, Tự động lấy nét theo pha Focus Pixels','Apple A18 Pro',8,'Sim kép (nano-Sim và e-Sim) - Hỗ trợ 2 e-Sim','iOS 18','6 lõi mới với 2 lõi hiệu năng và 4 lõi hiệu suất','Sạc không dây MagSafe lên đến 25W với bộ tiếp hợp 30W trở lên\r\nSạc không dây Qi2 lên đến 15W\r\nSạc không dây Qi lên đến 7,5W'),(11,8,'Chinh 200 MP - Phu 12 MP - 10 MP - FullHD 1080p 240fps - FullHD 1080p 120fps - 8K 4320p 30fps - 4K 2160p 120fps','10MP - F2/2','Snapdragon 8 Elite 3nm for Galaxy',12,'2 Nano-SIM','Android 16','8 nhân -  4GHz - 3GHz','Sạc nhanh 25W sạc không dây'),(12,6.9,'Camera chính: 48MP, f/1.78, 24mm, 2µm, chống rung quang học dịch chuyển cảm biến thế hệ thứ hai, Focus Pixels 100%\r\nTelephoto 2x 12MP: 52 mm, ƒ/1.6\r\nCamera góc siêu rộng: 48MP, 13 mm,ƒ/2.2 và trường ảnh 120°, Hybrid Focus Pixels, ảnh có độ phân giải','12MP, ƒ/1.9, Tự động lấy nét theo pha Focus Pixels','Apple A18 Pro',8,'Sim kép (nano-Sim và e-Sim) - Hỗ trợ 2 e-Sim','iOS 18','6 lõi mới với 2 lõi hiệu năng và 4 lõi hiệu suất','Sạc không dây MagSafe lên đến 25W với bộ tiếp hợp 30W trở lên\r\nSạc không dây Qi2 lên đến 15W\r\nSạc không dây Qi lên đến 7,5W'),(13,6.67,'Máy ảnh chính: 50MP, 23mm, ƒ/1 6\r\nMáy ảnh góc siêu rộng: 12MP, 15mm, ƒ/2 2\r\nMáy ảnh tele: 50MP, 60mm, ƒ/2 0','32MPƒ/2,0\r\nĐộ dài tiêu cự tương đương 25 mm\r\nFOV 80,8˚\r\nChế độ ban đêm\r\nHDR\r\nTính năng chụp ảnh bằng lòng bàn tay','MediaTek Dimensity 9300+',12,'Sim kép (nano-Sim và e-Sim) - Hỗ trợ 2 e-Sim','Android 14','1 x Cortex-X4, tối đa 3,4GHz; 3 x Cortex-X4, lên đến 2,85GHz; 4 x Cortex-A720, lên đến 2.0GHz','HyperCharge 120W\r\nHyperCharge không dây 50W'),(14,6.9,'Camera 48MP + 12MP tele','12MP','Apple A19 Pro',8,'2 eSIM','iOS 19','6 lõi','Sạc nhanh 30W'),(15,6.8,'200MP + 50MP + 12MP','40MP','Snapdragon 8 Gen 5',16,'2 Nano-SIM + eSIM','Android 16','10 nhân','Sạc nhanh 200W'),(16,6.7,'50MP + 48MP + 64MP','32MP','Dimensity 9400+',12,'2 Nano-SIM','Android 15','8 nhân','Sạc nhanh 120W'),(17,6.73,'200MP Leica','32MP','Snapdragon 8 Gen 4',16,'2 Nano-SIM','Android 15','8 nhân','Sạc nhanh 120W'),(18,6.7,'108MP + 12MP','32MP','Dimensity 8300 Ultra',12,'2 Nano-SIM','Android 15','8 nhân','Sạc nhanh 150W'),(19,6.78,'50MP + 50MP + 64MP','32MP','Snapdragon 8 Gen 4',16,'2 Nano-SIM','Android 15','8 nhân','Sạc nhanh 120W'),(25,6.78,'50MP + 50MP + 64MP','32MP','Snapdragon 8 Gen 4',16,'2 Nano-SIM','Android 15','8 nhân','Sạc nhanh 120W'),(26,6.7,'50MP + 48MP','32MP','Snapdragon 8 Gen 3',12,'2 Nano-SIM','Android 15','8 nhân','Sạc nhanh 150W'),(27,6.5,'50MP + 12MP','10.5MP','Google Tensor G4',8,'eSIM','Android 15','8 nhân','Sạc nhanh 30W'),(28,6.7,'50MP + 50MP','32MP','Kirin 9020',12,'2 Nano-SIM','HarmonyOS 5','8 nhân','Sạc nhanh 88W'),(35,6.1,'Hệ thống camera 2 trong 1\r\nFusion 48MP: 26 mm, khẩu độ ƒ/1.6, chống rung quang học, Hybrid Focus Pixels, ảnh có độ phân giải siêu cao (24MP và 48MP)\r\nĐồng thời hỗ trợ Telephoto 2x 12MP: 52 mm, khẩu độ ƒ/1.6, chống rung quang học, Hybrid Focus Pixels','Camera 12MP Khẩu độ ƒ/1.9\r\nCamera TrueDepth hỗ trợ nhận diện khuôn mặt','Chip A18',8,'Sim kép (nano-Sim và e-Sim) - Hỗ trợ 2 e-Sim','iOS 18','CPU 6 lõi mới với 2 lõi hiệu năng và 4 lõi tiết kiệm điện','Khả năng sạc nhanh Sạc lên đến 50% trong 30 phút với bộ tiếp hợp 20W trở lên'),(36,6.1,'Hệ thống camera 2 trong 1\r\nFusion 48MP: 26 mm, khẩu độ ƒ/1.6, chống rung quang học, Hybrid Focus Pixels, ảnh có độ phân giải siêu cao (24MP và 48MP)\r\nĐồng thời hỗ trợ Telephoto 2x 12MP: 52 mm, khẩu độ ƒ/1.6, chống rung quang học, Hybrid Focus Pixels','Camera 12MP Khẩu độ ƒ/1.9\r\nCamera TrueDepth hỗ trợ nhận diện khuôn mặt','Chip A18',8,'Sim kép (nano-Sim và e-Sim) - Hỗ trợ 2 e-Sim','iOS 18','CPU 6 lõi mới với 2 lõi hiệu năng và 4 lõi tiết kiệm điện','Khả năng sạc nhanh Sạc lên đến 50% trong 30 phút với bộ tiếp hợp 20W trở lên'),(37,8,'Chinh 200 MP - Phu 12 MP - 10 MP - FullHD 1080p 240fps - FullHD 1080p 120fps - 8K 4320p 30fps - 4K 2160p 120fps','10MP - F2/2','Snapdragon 8 Elite 3nm for Galaxy',12,'2 Nano-SIM','Android 16','8 nhân -  4GHz - 3GHz','Sạc nhanh 25W sạc không dây'),(38,8,'Chinh 200 MP - Phu 12 MP - 10 MP - FullHD 1080p 240fps - FullHD 1080p 120fps - 8K 4320p 30fps - 4K 2160p 120fps','10MP - F2/2','Snapdragon 8 Elite 3nm for Galaxy',12,'2 Nano-SIM','Android 16','8 nhân -  4GHz - 3GHz','Sạc nhanh 25W sạc không dây'),(39,6.9,'Chính 48 MP & Phụ 48 MP, 48 MP','18 MP','Apple A19 Pro 6 nhân',12,'1 Nano SIM & 1 eSIM','iOS 26','Apple A19 Pro 6 nhân','hỗ trợ sạc tối đa 40W'),(40,6.9,'Chính 48 MP & Phụ 48 MP, 48 MP','18 MP','Apple A19 Pro 6 nhân',12,'1 Nano SIM & 1 eSIM','iOS 26','Apple A19 Pro 6 nhân','hỗ trợ sạc tối đa 40W'),(41,6.9,'Chính 48 MP & Phụ 48 MP, 48 MP','18 MP','Apple A19 Pro 6 nhân',12,'1 Nano SIM & 1 eSIM','iOS 26','Apple A19 Pro 6 nhân','hỗ trợ sạc tối đa 40W'),(42,6.77,'HD 720p@30fps\r\nFullHD 1080p@30fps','32 MP','Dimensity 6300',12,'2 Nano SIM','Android 15','MediaTek Dimensity 6300 5G 8 nhân','hỗ trợ sạc tối đa 90W, sạc pin nhanh'),(43,6.77,'HD 720p@30fps\r\nFullHD 1080p@30fps','32 MP','Dimensity 6300',12,'2 Nano SIM','Android 15','MediaTek Dimensity 6300 5G 8 nhân','hỗ trợ sạc tối đa 90W, sạc pin nhanh'),(44,6.78,'Camera góc rộng: 50 MP, f/1.8, 1/1.95\", 0.8µm, PDAF, OIS\r\nCamera góc siêu rộng: 8 MP, f/2.2, 120˚, 1/4.0\", 1.12µm','32 MP HD Selfie Camera f/2.0, 84º FoV, 5P lens','Qualcomm SM6450 Snapdragon 6 Gen 1 (4 nm)',12,'2 SIM (Nano-SIM)','Android 14 OS','4x2.2 GHz Cortex-A78 & 4x1.8 GHz Cortex-A55','Sạc có dây 44W'),(45,6.3,'48 MP, f/1.7, 25mm (rộng), 1/2.0″, 0.8µm, PDAF điểm ảnh kép, OIS\r\n10.8 MP, f/3.1, 112mm (tele), 1/3.2″, PDAF điểm ảnh kép, OIS, zoom quang 5x, 13 MP, 120˚ (góc siêu rộng), 1/3.1″, PDAF','10,5 MP, f/2.2, 95˚, 20mm (góc siêu rộng), 1/3,1″, 1,22µm, PDAF','Google Tensor G5 (4nm)',12,'2 SIM (Nano-SIM + eSIM)','Android 16','Octa-core (1x 3.1 GHz – Cortex-X4 & 3x 2.6 GHz – Cortex-A720 & 4x 1.92 GHz – Cortex-A520)','Sạc có dây 30W, PD3.0, PPS, sạc 55% trong 30 phút, \r\nSạc không dây 15W (từ tính), \r\nQi2 Sạc không dây ngược'),(46,6.3,'48 MP, f/1.7, 25mm (rộng), 1/2.0″, 0.8µm, PDAF điểm ảnh kép, OIS\r\n10.8 MP, f/3.1, 112mm (tele), 1/3.2″, PDAF điểm ảnh kép, OIS, zoom quang 5x, 13 MP, 120˚ (góc siêu rộng), 1/3.1″, PDAF','10,5 MP, f/2.2, 95˚, 20mm (góc siêu rộng), 1/3,1″, 1,22µm, PDAF','Google Tensor G5 (4nm)',12,'2 SIM (Nano-SIM + eSIM)','Android 16','Octa-core (1x 3.1 GHz – Cortex-X4 & 3x 2.6 GHz – Cortex-A720 & 4x 1.92 GHz – Cortex-A520)','Sạc có dây 30W, PD3.0, PPS, sạc 55% trong 30 phút, \r\nSạc không dây 15W (từ tính), \r\nQi2 Sạc không dây ngược');
/*!40000 ALTER TABLE `specifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `cccd` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dob` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_at` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'User2','user2@gmail.com','0138291382','$2a$10$yXRBTaDX0.civ8SP2.DDSeFJq0/eRju5AkO9YwyNfMIYOC0keb7zq','034984922085','ABC, XYZ','Khách hàng','03/04/2001','Nam','Hoạt động','06/05/2025','23/08/2025',5,5),(3,'User3','user3@gmail.com','01382913821','1234','034984922087','ABC, XYZ','Khách hàng','03/04/2001','Nam','Hoạt động','06/05/2025','12/06/2025',5,5),(4,'User4','user4@gmail.com','0138291382','$2a$10$XiuVUtNBmiUnDfXWBjjZLuduUO57VBo2wT31gZm1VhV6Gg/OJd9rW','034984922088','ABC, XYZ','Khách hàng','03/04/2001','Nam','Khóa','06/05/2025','05/07/2025',5,5),(5,'Admin1','admin1@gmail.com','0138291382','$2a$10$jdTKPli3jiSg6GFWRrOjce0GObuPOsaTp3iDXfluiSKsW3LpAY18y','034984922089','ABC, XYZ','Admin','03/04/2001','Nam','Hoạt động','06/05/2025','16/09/2025',5,5),(6,'nguyen duc','nguyenduc@gmail.com','0664856215','$2a$10$DdkpRbaJRn8kdEtvQ1f5tuexPRqMqXKSFSWytBKju8uprDWGL2TYC','035845235845','ABC, XYZ','Khách hàng','17/12/2005','Nam','Hoạt động','03/07/2025','18/09/2025',5,6),(7,'test1','test1@gmail.com','0254784652','$2a$10$zXaUVRHTqa5ZyZRrWY.ELeYUFSTSEuEZssfbnWnO0nFuScBLKslPG','035745685132','ABC, XYZ','Khách hàng','17/01/2007','Nam','Hoạt động','01/08/2025','01/08/2025',5,5),(8,'Nguyễn Thị H','nguyenthih@gmail.com','0354684561','$2a$10$..DoVAdVlVPpkLAxaPJKyOzO3iqDUcF4hObC1yQGrbeRzaxAxWT4u','038668965712','ABC, XYZ','Khách hàng','12/06/2003','Nữ','Hoạt động','02/08/2025','02/08/2025',5,5),(9,'Nguyễn Văn A','nguyenvana@gmail.com','0345864523','$2a$10$R2BaOlKEqSgbgWLFmH5sA.AXUGCc3EVw98vYcuwjD5U5BqfG7BXcC','024687365421','ABC, XYZ','Khách hàng','09/03/2002','Nam','Hoạt động','01/08/2025','01/08/2025',5,5),(10,'Nguyễn Như Q','nguyennhuq@gmail.com','0254562368','$2a$10$yqbvbpcZxDlnf1BqMk1zJ.UAtoi07LvjNsWelmB8qiklKChbReLd6','038796423548','Phố Nguyễn Thiếp, Kim Phú, Yên Sơn, Tuyên Quang, Việt Nam','Khách hàng','18/07/2001','Nữ','Hoạt động','01/08/2025','05/08/2025',5,5),(11,'Nguyễn Trung Đức','nguyentrungduc@gmail.com','0123548645','$2a$10$a/qvD.tBGAfaup9dngfbSuycPC1w3oYSB8CE7PD/i69wsU.LtUtu6','024687654216','Mỹ Đình II, Nam Từ Liêm, Hà Nội.','Khách hàng','26/11/2003','Nam','Hoạt động','04/08/2025','05/08/2025',5,5),(12,'Đăng Thiên Long','dangthienlong@gmail.com','0965478235','$2a$10$ALi8SrQqq1jxC6dJ2Wn6Du4kj5Y.YNn/G850RP4HWI6DfVFRN7U5a','039874563214','Số 8, Nguyễn Cơ Thạch, Nam Từ Liêm, Hà Nội','Khách hàng','12/09/2000','Nam','Hoạt động','12/08/2025','12/08/2025',5,5),(13,'Lê Văn Thiên','levanthien@gmail.com','0549875621','$2a$10$h29ZPnjay1oaW3R1uytzgeeU58p0IvjWnGOIhK1TERP2Dw2LR0Kiu','056487632514','ABC, XYZ','Khách hàng','08/09/2005','Nam','Hoạt động','13/08/2025','13/08/2025',5,5),(14,'Nguyễn Thị Trang','nguyenthitrang@gmail.com','0246487512','$2a$10$KMSbQsRPHLH4nhmm2sEIjev/0VmwdefeECghND/iY/8JOy/3ljiKi','035487951207','ABC, XYZ','Khách hàng','07/02/2004','Nữ','Hoạt động','15/08/2025','15/08/2025',5,5),(15,'Đặng Thị Thùy Linh','dangthithuylinh@gmail.com','0354769875','$2a$10$DB2phj0behPkUGaymON7D.10QIpiTWKi4po6Fls1jwEstwCvbO2pm','035471254789','ABC, XYZ','Khách hàng','17/02/2003','Nữ','Hoạt động','17/08/2025','20/08/2025',5,5),(16,'Nguyễn Thanh Lâm','nguyenthanhlam@gmail.com','0952145268','$2a$10$sgzlNV/XCAJzxrEnP2QCLOPx4pOkaZjkEkJMJJBkHH4xTA3987Rky','035789541254','ABC, XYZ','Khách hàng','09/01/2009','Nam','Hoạt động','18/08/2025','20/08/2025',5,5),(17,'Phạm Thị Lan','phamthilan@gmail.com','0356547895','$2a$10$3BinCun7sEt8xJIRsU37k.XJyVqVCfdnOvzqweTNV3GQMenkuI/X.','024657895412','XYZ, ABC','Khách hàng','09/02/2008','Nữ','Hoạt động','18/08/2025','18/08/2025',5,5),(18,'Nguyễn Văn An','nguyenvanan@gmail.com','0365478965','$2a$10$Us9Ja7VTweQITl.ZNuGc.eJB0mevPPk7m1MpQlThRsyWnFtCXkR2S','035456874565','ABC, XYZ','Khách hàng','08/03/2003','Nam','Hoạt động','18/08/2025','18/08/2025',5,5),(20,'Nguyễn Nhân Viên','nguyennhanvien@gmail.com','0354879569','$2a$10$jeC26/U6670.q/hz0WJeXOX7rEJCXMhDluMrraQ1fTQEfjWNowDMy','032548795215','ABC, XYZ','Nhân viên','02/05/2001','Nam','Hoạt động','18/08/2025','22/08/2025',5,5),(22,'Phan Thị Nhân Viên','phanthinhanvien@gmail.com','0987456235','$2a$10$FkMPPLGd4WkQ8rKr1/q9Xe10qL161cINNm5cLDRqKA/c0Tn2iBUhe','032468754123','ABC, XYZ','Nhân viên','08/09/2002','Nữ','Hoạt động','18/08/2025','20/08/2025',5,5),(23,'Nguyễn Văn L','nguyenvanl@gmail.com','0937183917','$2a$10$XEVw7b7HpnXt8St57M.yhOXvF.1T30NP236JvK5r1V8HbOjKCD34u','038917009312','ABC, XYZ','Nhân viên','06/02/2004','Nam','Hoạt động','20/08/2025','23/08/2025',5,5),(24,'Phạm Minh Quang','phamminhquang@gmail.com','0938917482','$2a$10$quGC.epn24JNWRHJUDrrDO4FMQBEMHrd4RcWVc2n/KVbN1eUb4stC','093748173212','ABC, XYZ','Khách hàng','28/05/2003','Nam','Hoạt động','25/08/2025','25/08/2025',5,5),(25,'Nguyễn Văn Quang','nguyenvanquang@gmail.com','0973819371','$2a$10$.EFGOXTx3yHDZKQkO1d2huHINEDTT1WSS.ytdN.nEmiDfGgCBSE8C','036820038123','ABC,XYZ','Khách hàng','12/09/2007','Nam','Hoạt động','25/08/2025','25/08/2025',5,5),(26,'Nguyễn Văn Lam','trungduc26112003@gmail.com','0978378139','$2a$10$h7e066hclNQC5f0uX6S5IOn/drp5yamJ10NeqYlHIKwO1cvsbZu4a','037829018312','abc, xyz','Khách hàng','20/11/2003','Nam','Hoạt động','22/09/2025','23/09/2025',NULL,26),(27,'Nguyễn Văn Thực','superpenta4@gmail.com','0938493718','$2a$10$4OwxXSf8eeIwDueAXEwAmuuXdCDUhRiTmik3TcZa6amjd5JzlKf2.','039401382912','Số 8 Tràng Thi, Phường Hàng Trống, Quận Hoàn Kiếm, Hà Nội','Khách hàng','17/12/2003','Nam','Hoạt động','27/09/2025','08/10/2025',NULL,27),(28,'Đỗ Đại Học','ductrungnguyenitech@gmail.com','0329867213','$2a$10$6cTO5YaZZhE8u7jdwFCsr.yUDODvVCpQccXVJdAuHiXFSaI8Yc0CK','039478746232','Số 12 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh','Khách hàng','18/08/2002','Nam','Hoạt động','27/09/2025','27/09/2025',NULL,28),(29,'nguyen van doc','nguyenvandoc@gmail.com','0328379381','$2a$10$y008VRlZBmxFy4ROY9UMxeZ.dJcAmOhnTc3L415urVDuug/sm8f.S','039209389212','abc','Khách hàng','23/11/2003','Nam','Hoạt động','27/09/2025','27/09/2025',NULL,29),(31,'dangvanninh','dangvanninh@gmail.com','0327819732','$2a$10$Ga2O7S6RV86O7MuUuzXWCO80tqpIEZHIQ9XqKkLifggoxB47EeQOy','037891829300','ABC, XYZ','Khách hàng','30/06/2005','Nam','Hoạt động','04/10/2025','04/10/2025',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-08  2:17:46

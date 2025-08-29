package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.StatisticalReport.ReportFilterRequest;
import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.StatisticalReport.TopCustomerSpendingResponse;
import com.example.SellPhone.Repository.OrderItemRepository;
import com.example.SellPhone.Repository.OrderRepository;
import com.example.SellPhone.Repository.ProductRepository;
import com.example.SellPhone.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticalReportService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    // Báo cáo doanh thu start
    // Tính tổng doanh thu trong khoảng thời gian với trạng thái 'hoàn thành'
    public Long calculateTotalRevenue(String startDate, String endDate) {
        Long totalRevenue = orderRepository.calculateTotalRevenue(startDate, endDate);
        if (totalRevenue == null) {
            return 0L; // Trả về 0 nếu không có doanh thu
        }
        return totalRevenue;
    }

    // Đếm tổng số lượng đơn hàng trong khoảng thời gian
    public int countByDeliveryTimeEndBetween(String startDate, String endDate) {
        return orderRepository.countByDeliveryTimeEndBetween(startDate, endDate);
    }

    // Tính trung bình giá trị đơn hàng trong khoảng thời gian
    public Long calculateAverageOrderAmount(String startDate, String endDate) {
        Long averageOrderAmount = orderRepository.calculateAverageOrderAmount(startDate, endDate);
        if (averageOrderAmount == null) {
            return 0L; // Trả về 0 nếu không có giá trị trung bình
        }
        return averageOrderAmount;
    }

    // Đếm tổng số đơn hàng có trạng thái là 'Đã hủy'
    public int countByCanceledOrderStatusAndDeliveryTimeEndBetween(String startDate, String endDate) {
        return orderRepository.countByOrderStatusAndDeliveryTimeEndBetween("Đã hủy", startDate, endDate);
    }

    // Tính tổng doanh thu kỳ trước và kỳ này
    public Map<String, Long> calculateTotalRevenueForPreviousAndCurrentPeriod(String startDate, String endDate) {
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Chuyển đổi từ String sang LocalDate để sử dụng cho ChronoUnit
        LocalDate start = LocalDate.parse(startDate, dbFormatter);
        LocalDate end = LocalDate.parse(endDate, dbFormatter);

        // Tính ngày bắt đầu và ngày kết thúc của kỳ trước có cùng độ dài với kỳ hiện tại
        long days = ChronoUnit.DAYS.between(start, end);
        LocalDate previousStart = start.minusDays(days + 1);
        LocalDate previousEnd = start.minusDays(1);

        String previousStartDb = previousStart.format(dbFormatter);
        String previousEndDb = previousEnd.format(dbFormatter);

        Long currentRevenue = orderRepository.calculateTotalRevenue(startDate, endDate);
        Long previousRevenue = orderRepository.calculateTotalRevenue(previousStartDb, previousEndDb);

        Map<String, Long> revenueMap = new LinkedHashMap<>();
        revenueMap.put("Kỳ này", currentRevenue != null ? currentRevenue : 0L);
        revenueMap.put("Kỳ trước", previousRevenue != null ? previousRevenue : 0L);
        return revenueMap;
    }


    // Đếm số lượng đơn hàng theo phương thức thanh toán
    public Map<String, Integer> countOrdersByPaymentMethod(String startDate, String endDate) {
        List<Object[]> paymentMethodCounts = orderRepository.countOrdersByPaymentMethod(startDate, endDate);
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Object[] row : paymentMethodCounts) {
            result.put((String) row[0], ((Number) row[1]).intValue());
        }
        return result;
    }

    // Tính doanh thu theo danh mục sản phẩm
    public Map<String, Long> calculateTotalRevenueByCategory(String startDate, String endDate) {
        List<Object[]> categoryRevenueList = orderItemRepository.calculateTotalRevenueByCategory(startDate, endDate);
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : categoryRevenueList) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    // Tính doanh thu theo thời gian (biểu đồ đường)
    public Map<String, Long> getDailyRevenue(String startDate, String endDate) {
        List<Object[]> dailyRevenueList = orderRepository.getDailyRevenue(startDate, endDate);
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : dailyRevenueList) {
            result.put((row[0]).toString(), ((Number) row[1]).longValue());
        }
        return result;
    }
    // Báo cáo doanh thu end

    // Báo cáo sản phẩm start
    // Lấy danh sách sản phẩm bán chạy nhất
    public List<BestSellingProductResponse> getBestSellingProductsCurrentMonth(String startDate, String endDate) {
        return orderItemRepository.getBestSellingProductsInPeriod(startDate, endDate);
    }

    // Lấy danh sách sản phẩm số lượng bán ra thấp
    public List<BestSellingProductResponse> getLowSellingProducts(String startDate, String endDate) {
        return productRepository.getLowSellingProductsInPeriod(startDate, endDate);
    }
    // Báo cáo sản phẩm end

    // Báo cáo khách hàng start
    // Tổng số khách hàng còn hoạt động đến thòi điểm endDate
    public int countActiveCustomersCreatedBefore(String endDate){
        return userRepository.countActiveCustomersCreatedBefore(endDate);
    }

    // Tổng số khách hàng mới trong khoảng thời gian từ startDate đến endDate
    public int countNewCustomersBetween(String startDate, String endDate) {
        return userRepository.countCustomersByCreatedAtBetween(startDate, endDate);
    }

    // Đơn hàng trung bình/ KH
    public String calculateAverageOrdersPerCustomer(String startDate, String endDate) {
        int totalOrders = orderRepository.countByDeliveryTimeEndBetween(startDate, endDate);
        int totalCustomers = userRepository.countActiveCustomersCreatedBefore(endDate);
        if (totalCustomers == 0) {
            return "0"; // Tránh chia cho 0
        }
        return String.format("%.3f", (double) totalOrders / totalCustomers); // Làm tròn đến 3 chữ số thập phân
    }

    // Top 10 khách hàng có tổng chi tiêu cao nhất
    public List<TopCustomerSpendingResponse> findTop10SpendingCustomersWithOrderCount(String startDate, String endDate) {
        return userRepository.findTop10SpendingCustomersWithOrderCount(startDate, endDate);
    }

    // Phân loại khách hàng theo chi tiêu
    public Map<String, Integer> classifyCustomersBySpending(String startDate, String endDate) {
        Map<String, Integer> customerClassification = new LinkedHashMap<>();
        customerClassification.put("Dưới 5 triệu", userRepository.countCustomersUnder5M(startDate, endDate));
        customerClassification.put("Từ 5 đến 20 triệu", userRepository.countCustomersFrom5MTo20M(startDate, endDate));
        customerClassification.put("Từ 20 đến 50 triệu", userRepository.countCustomersFrom20MTo50M(startDate, endDate));
        customerClassification.put("Trên 50 triệu", userRepository.countCustomersAbove50M(startDate, endDate));
        return customerClassification;
    }
    // Báo cáo khách hàng end

    // Xuất excel
    // Doanh thu
    public void exportRevenueReportToExcel(ReportFilterRequest filter, HttpServletResponse response) {
        // Lấy dữ liệu từ các hàm đã có
        String startDate = filter.getStartDate();
        String endDate = filter.getEndDate();
        Long totalRevenue = calculateTotalRevenue(startDate, endDate);
        int totalOrders = countByDeliveryTimeEndBetween(startDate, endDate);
        Long avgOrderValue = calculateAverageOrderAmount(startDate, endDate);
        int canceledOrders = countByCanceledOrderStatusAndDeliveryTimeEndBetween(startDate, endDate);
        Map<String, Integer> paymentMethod = countOrdersByPaymentMethod(startDate, endDate);
        Map<String, Long> revenueByCategory = calculateTotalRevenueByCategory(startDate, endDate);
        Map<String, Long> revenueByDay = getDailyRevenue(startDate, endDate);

        Workbook workbook = new XSSFWorkbook();

        // --- Tạo font và style ---

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyleLeft = workbook.createCellStyle();
        dataStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        dataStyleLeft.setBorderBottom(BorderStyle.THIN);
        dataStyleLeft.setBorderTop(BorderStyle.THIN);
        dataStyleLeft.setBorderLeft(BorderStyle.THIN);
        dataStyleLeft.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyleRight = workbook.createCellStyle();
        dataStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        dataStyleRight.setBorderBottom(BorderStyle.THIN);
        dataStyleRight.setBorderTop(BorderStyle.THIN);
        dataStyleRight.setBorderLeft(BorderStyle.THIN);
        dataStyleRight.setBorderRight(BorderStyle.THIN);

        // Định dạng tiền tệ Việt Nam
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(dataStyleRight);
        currencyStyle.setDataFormat(dataFormat.getFormat("#,##0 ₫"));

        // --- 1. Sheet Tổng Quan ---
        Sheet overviewSheet = workbook.createSheet("Tổng Quan");
        Row headerRow = overviewSheet.createRow(0);
        Cell headerCell0 = headerRow.createCell(0);
        headerCell0.setCellValue("Thông tin");
        headerCell0.setCellStyle(headerStyle);
        Cell headerCell1 = headerRow.createCell(1);
        headerCell1.setCellValue("Giá trị");
        headerCell1.setCellStyle(headerStyle);

        String[] infoKeys = {"Từ ngày", "Đến ngày", "Tổng doanh thu", "Tổng số đơn hàng", "Giá trị TB mỗi đơn", "Tổng số đơn bị huỷ"};
        Object[] infoValues = {startDate, endDate, totalRevenue, totalOrders, avgOrderValue, canceledOrders};

        for (int i = 0; i < infoKeys.length; i++) {
            Row row = overviewSheet.createRow(i + 1);
            Cell keyCell = row.createCell(0);
            keyCell.setCellValue(infoKeys[i]);
            keyCell.setCellStyle(dataStyleLeft);

            Cell valueCell = row.createCell(1);
            if (infoValues[i] instanceof Long longVal) {
                valueCell.setCellValue(longVal);
                if (i == 2 || i == 4) {  // Tổng doanh thu và giá trị TB mỗi đơn: áp dụng định dạng tiền
                    valueCell.setCellStyle(currencyStyle);
                } else {
                    valueCell.setCellStyle(dataStyleRight);
                }
            } else if (infoValues[i] instanceof Integer intVal) {
                valueCell.setCellValue(intVal);
                valueCell.setCellStyle(dataStyleRight);
            } else if (infoValues[i] instanceof String strVal) {
                valueCell.setCellValue(strVal);
                valueCell.setCellStyle(dataStyleLeft);
            }
        }

        // --- 2. Sheet Doanh Thu Theo Phương Thức Thanh Toán ---
        Sheet paymentSheet = workbook.createSheet("Theo Phương Thức Thanh Toán");
        Row paymentHeader = paymentSheet.createRow(0);
        Cell paymentHeaderCell0 = paymentHeader.createCell(0);
        paymentHeaderCell0.setCellValue("Phương thức thanh toán");
        paymentHeaderCell0.setCellStyle(headerStyle);
        Cell paymentHeaderCell1 = paymentHeader.createCell(1);
        paymentHeaderCell1.setCellValue("Doanh thu");
        paymentHeaderCell1.setCellStyle(headerStyle);

        int rowIdx = 1;
        for (Map.Entry<String, Integer> entry : paymentMethod.entrySet()) {
            Row row = paymentSheet.createRow(rowIdx++);
            Cell cellKey = row.createCell(0);
            cellKey.setCellValue(entry.getKey());
            cellKey.setCellStyle(dataStyleLeft);

            Cell cellValue = row.createCell(1);
            cellValue.setCellValue(entry.getValue());
            cellValue.setCellStyle(dataStyleRight);
        }

        // --- 3. Sheet Doanh Thu Theo Danh Mục Sản Phẩm ---
        Sheet categorySheet = workbook.createSheet("Theo Danh Mục Sản Phẩm");
        Row categoryHeader = categorySheet.createRow(0);
        Cell catHeader0 = categoryHeader.createCell(0);
        catHeader0.setCellValue("Danh mục sản phẩm");
        catHeader0.setCellStyle(headerStyle);
        Cell catHeader1 = categoryHeader.createCell(1);
        catHeader1.setCellValue("Doanh thu");
        catHeader1.setCellStyle(headerStyle);

        rowIdx = 1;
        for (Map.Entry<String, Long> entry : revenueByCategory.entrySet()) {
            Row row = categorySheet.createRow(rowIdx++);
            Cell cellKey = row.createCell(0);
            cellKey.setCellValue(entry.getKey());
            cellKey.setCellStyle(dataStyleLeft);

            Cell cellValue = row.createCell(1);
            cellValue.setCellValue(entry.getValue());
            cellValue.setCellStyle(currencyStyle);
        }

        // --- 4. Sheet Doanh Thu Theo Thời Gian ---
        Sheet timeSheet = workbook.createSheet("Theo Thời Gian");
        Row timeHeader = timeSheet.createRow(0);
        Cell timeHeader0 = timeHeader.createCell(0);
        timeHeader0.setCellValue("Ngày");
        timeHeader0.setCellStyle(headerStyle);
        Cell timeHeader1 = timeHeader.createCell(1);
        timeHeader1.setCellValue("Doanh thu");
        timeHeader1.setCellStyle(headerStyle);

        rowIdx = 1;
        for (Map.Entry<String, Long> entry : revenueByDay.entrySet()) {
            Row row = timeSheet.createRow(rowIdx++);
            Cell cellKey = row.createCell(0);
            cellKey.setCellValue(entry.getKey());
            cellKey.setCellStyle(dataStyleLeft);

            Cell cellValue = row.createCell(1);
            cellValue.setCellValue(entry.getValue());
            cellValue.setCellStyle(currencyStyle);
        }

        // --- Tự động giãn cột cho các sheet ---
        for (int i = 0; i < 2; i++) {
            overviewSheet.autoSizeColumn(i);
            paymentSheet.autoSizeColumn(i);
            categorySheet.autoSizeColumn(i);
            timeSheet.autoSizeColumn(i);
        }

        // Thiết lập header để xuất file excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BaoCaoDoanhThu_TDMobile.xlsx");
        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi ghi file excel", e);
        }
    }

    // Sản phẩm
    public void exportBestSellersReportToExcel(ReportFilterRequest filter, HttpServletResponse response) {
        String startDate = filter.getStartDate();
        String endDate = filter.getEndDate();

        List<BestSellingProductResponse> bestSellingProducts = getBestSellingProductsCurrentMonth(startDate, endDate);
        List<BestSellingProductResponse> lowSellingProducts = getLowSellingProducts(startDate, endDate);

        Workbook workbook = new XSSFWorkbook();

        // --- Tạo font và style ---
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyleLeft = workbook.createCellStyle();
        dataStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        dataStyleLeft.setBorderBottom(BorderStyle.THIN);
        dataStyleLeft.setBorderTop(BorderStyle.THIN);
        dataStyleLeft.setBorderLeft(BorderStyle.THIN);
        dataStyleLeft.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyleRight = workbook.createCellStyle();
        dataStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        dataStyleRight.setBorderBottom(BorderStyle.THIN);
        dataStyleRight.setBorderTop(BorderStyle.THIN);
        dataStyleRight.setBorderLeft(BorderStyle.THIN);
        dataStyleRight.setBorderRight(BorderStyle.THIN);

        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(dataStyleRight);
        currencyStyle.setDataFormat(dataFormat.getFormat("#,##0 ₫"));

        // --- 1. Sheet Sản Phẩm Bán Chạy ---
        Sheet bestSellerSheet = workbook.createSheet("Top Bán Chạy");

        Row bestHeader = bestSellerSheet.createRow(0);
        String[] bestHeaders = {"Tên sản phẩm", "Màu sắc", "ROM", "Giá bán", "Số lượng bán"};
        for (int i = 0; i < bestHeaders.length; i++) {
            Cell cell = bestHeader.createCell(i);
            cell.setCellValue(bestHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (BestSellingProductResponse p : bestSellingProducts) {
            Row row = bestSellerSheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(p.getName());
            row.getCell(0).setCellStyle(dataStyleLeft);

            row.createCell(1).setCellValue(p.getColor());
            row.getCell(1).setCellStyle(dataStyleLeft);

            String formattedRom = formatRom(p.getRom());
            row.createCell(2).setCellValue(formattedRom);
            row.getCell(2).setCellStyle(dataStyleLeft);

            Cell revenueCell = row.createCell(3);
            revenueCell.setCellValue(p.getSellingPrice());
            revenueCell.setCellStyle(currencyStyle);

            Cell quantityCell = row.createCell(4);
            quantityCell.setCellValue(p.getTotalQuantity());
            quantityCell.setCellStyle(dataStyleRight);
        }

        // --- 2. Sheet Sản Phẩm Bán Ít ---
        Sheet lowSellerSheet = workbook.createSheet("Bán Ít Nhất");

        Row lowHeader = lowSellerSheet.createRow(0);
        String[] lowHeaders = {"Tên sản phẩm", "Màu sắc", "ROM", "Giá bán", "Số lượng bán"};
        for (int i = 0; i < lowHeaders.length; i++) {
            Cell cell = lowHeader.createCell(i);
            cell.setCellValue(lowHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        rowIdx = 1;
        for (BestSellingProductResponse p : lowSellingProducts) {
            Row row = lowSellerSheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(p.getName());
            row.getCell(0).setCellStyle(dataStyleLeft);

            row.createCell(1).setCellValue(p.getColor());
            row.getCell(1).setCellStyle(dataStyleLeft);

            String formattedRom = formatRom(p.getRom());
            row.createCell(2).setCellValue(formattedRom);
            row.getCell(2).setCellStyle(dataStyleLeft);

            Cell revenueCell = row.createCell(3);
            revenueCell.setCellValue(p.getSellingPrice());
            revenueCell.setCellStyle(currencyStyle);

            Cell quantityCell = row.createCell(4);
            quantityCell.setCellValue(p.getTotalQuantity());
            quantityCell.setCellStyle(dataStyleRight);
        }

        // --- Auto size các cột ---
        for (int i = 0; i < 5; i++) {
            bestSellerSheet.autoSizeColumn(i);
            lowSellerSheet.autoSizeColumn(i);
        }

        // --- Thiết lập header cho file Excel ---
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BaoCaoSanPham_TDMobile.xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi ghi file Excel", e);
        }
    }
    private String formatRom(Long rom) {
        if (rom >= 1000) {
            return rom % 1000 == 0
                    ? (rom / 1000) + "TB"
                    : String.format("%.1fTB", rom / 1000.0);
        }
        return rom + "GB";
    }

    // Khách hàng
    public void exportCustomersReportToExcel(ReportFilterRequest filter, HttpServletResponse response) {
        String startDate = filter.getStartDate();
        String endDate = filter.getEndDate();

        int totalCustomers = countActiveCustomersCreatedBefore(endDate);
        int totalNewCustomers = countNewCustomersBetween(startDate, endDate);
        String averageOrders = calculateAverageOrdersPerCustomer(startDate, endDate);
        List<TopCustomerSpendingResponse> topCustomers = findTop10SpendingCustomersWithOrderCount(startDate, endDate);
        Map<String, Integer> customerSegments = classifyCustomersBySpending(startDate, endDate);

        Workbook workbook = new XSSFWorkbook();

        // --- Font + style ---
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyleLeft = workbook.createCellStyle();
        dataStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        dataStyleLeft.setBorderBottom(BorderStyle.THIN);
        dataStyleLeft.setBorderTop(BorderStyle.THIN);
        dataStyleLeft.setBorderLeft(BorderStyle.THIN);
        dataStyleLeft.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyleRight = workbook.createCellStyle();
        dataStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        dataStyleRight.setBorderBottom(BorderStyle.THIN);
        dataStyleRight.setBorderTop(BorderStyle.THIN);
        dataStyleRight.setBorderLeft(BorderStyle.THIN);
        dataStyleRight.setBorderRight(BorderStyle.THIN);

        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(dataStyleRight);
        currencyStyle.setDataFormat(dataFormat.getFormat("#,##0 ₫"));

        // --- 1. Sheet Tổng Quan ---
        Sheet overviewSheet = workbook.createSheet("Tổng Quan");

        // Tạo header giống báo cáo doanh thu
        Row headerRow = overviewSheet.createRow(0);
        Cell headerCell0 = headerRow.createCell(0);
        headerCell0.setCellValue("Thông tin");
        headerCell0.setCellStyle(headerStyle);

        Cell headerCell1 = headerRow.createCell(1);
        headerCell1.setCellValue("Giá trị");
        headerCell1.setCellStyle(headerStyle);

        // Dữ liệu
        String[] infoKeys = {"Từ ngày", "Đến ngày", "Tổng số khách hàng", "Khách hàng mới", "Đơn hàng trung bình / khách"};
        Object[] infoValues = {startDate, endDate, totalCustomers, totalNewCustomers, averageOrders};

        for (int i = 0; i < infoKeys.length; i++) {
            Row row = overviewSheet.createRow(i + 1);

            Cell keyCell = row.createCell(0);
            keyCell.setCellValue(infoKeys[i]);
            keyCell.setCellStyle(dataStyleLeft);

            Cell valueCell = row.createCell(1);
            Object val = infoValues[i];

            if (val instanceof Integer intVal) {
                valueCell.setCellValue(intVal);
                valueCell.setCellStyle(dataStyleRight);
            } else if (val instanceof String strVal) {
                try {
                    double num = Double.parseDouble(strVal.replace(",", "."));
                    valueCell.setCellValue(num);
                    valueCell.setCellStyle(dataStyleRight);
                } catch (NumberFormatException e) {
                    valueCell.setCellValue(strVal);
                    valueCell.setCellStyle(dataStyleLeft);
                }
            } else {
                valueCell.setCellValue(val.toString());
                valueCell.setCellStyle(dataStyleLeft);
            }
        }

        // --- 2. Sheet Top 10 khách hàng ---
        Sheet topSheet = workbook.createSheet("Top KH Tiềm Năng");

        Row topHeader = topSheet.createRow(0);
        String[] headers = {"Hạng", "Mã KH", "Tên khách hàng", "Tổng chi tiêu", "Số đơn hàng"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = topHeader.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        int rank = 1;
        for (TopCustomerSpendingResponse c : topCustomers) {
            Row row = topSheet.createRow(rowIdx++);

            Cell rankCell = row.createCell(0);
            rankCell.setCellValue(rank++);
            rankCell.setCellStyle(dataStyleRight);

            Cell idCell = row.createCell(1);
            idCell.setCellValue(c.getUserId());
            idCell.setCellStyle(dataStyleRight);

            Cell nameCell = row.createCell(2);
            nameCell.setCellValue(c.getFullname());
            nameCell.setCellStyle(dataStyleLeft);

            Cell spentCell = row.createCell(3);
            spentCell.setCellValue(c.getTotalSpent());
            spentCell.setCellStyle(currencyStyle);

            Cell ordersCell = row.createCell(4);
            ordersCell.setCellValue(c.getTotalOrders());
            ordersCell.setCellStyle(dataStyleRight);
        }

        // --- 3. Sheet Phân Loại KH ---
        Sheet classifySheet = workbook.createSheet("Phân Loại KH");

        Row classifyHeader = classifySheet.createRow(0);
        classifyHeader.createCell(0).setCellValue("Phân loại");
        classifyHeader.getCell(0).setCellStyle(headerStyle);
        classifyHeader.createCell(1).setCellValue("Số lượng KH");
        classifyHeader.getCell(1).setCellStyle(headerStyle);

        int classifyRowIdx = 1;
        for (Map.Entry<String, Integer> entry : customerSegments.entrySet()) {
            Row row = classifySheet.createRow(classifyRowIdx++);

            Cell typeCell = row.createCell(0);
            typeCell.setCellValue(entry.getKey());
            typeCell.setCellStyle(dataStyleLeft);

            Cell countCell = row.createCell(1);
            countCell.setCellValue(entry.getValue());
            countCell.setCellStyle(dataStyleRight);
        }

        // --- Auto-size ---
        for (int i = 0; i < 5; i++) {
            topSheet.autoSizeColumn(i);
        }
        classifySheet.autoSizeColumn(0);
        classifySheet.autoSizeColumn(1);
        overviewSheet.autoSizeColumn(0);
        overviewSheet.autoSizeColumn(1);

        // --- Ghi file ---
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BaoCaoKhachHang_TDMobile.xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi ghi file Excel", e);
        }
    }

    // Xuất excel end

}

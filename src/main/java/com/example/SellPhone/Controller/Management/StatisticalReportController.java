package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Request.StatisticalReport.ReportFilterRequest;
import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.StatisticalReport.TopCustomerSpendingResponse;
import com.example.SellPhone.Service.StatisticalReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/management/statistical-report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticalReportController {
    StatisticalReportService reportService;

    @GetMapping
    public String managementStatisticalReport(Model model) {
        // Trả về trang báo cáo thống kê của quản trị viên
        model.addAttribute("currentPage", "statisticals");
        model.addAttribute("filter", new ReportFilterRequest());
        model.addAttribute("selectedReportType", "revenue");
        return "DashBoard/statistical-report";
    }

    @GetMapping("/filter")
    public String handleFilter(@ModelAttribute("filter") ReportFilterRequest filter, @RequestParam(value = "action", required = false) String action,
            HttpServletResponse response,
            Model model) throws IOException {
        model.addAttribute("currentPage", "statisticals");

        boolean hasError = false;

        if (filter.getStartDate() == null || filter.getStartDate().isEmpty()) {
            model.addAttribute("errorStartDate", "Vui lòng chọn ngày bắt đầu");
            hasError = true;
        }

        if (filter.getEndDate() == null || filter.getEndDate().isEmpty()) {
            model.addAttribute("errorEndDate", "Vui lòng chọn ngày kết thúc");
            hasError = true;
        }

        if(!hasError) {
            try {
                // Parse chuỗi từ input HTML thành LocalDate theo định dạng yyyy-MM-dd
                DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start = LocalDate.parse(filter.getStartDate(), inputFormat);
                LocalDate end = LocalDate.parse(filter.getEndDate(), inputFormat);

                if (start.isAfter(end)) {
                    model.addAttribute("errorStartDate", "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
                    model.addAttribute("errorEndDate", "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu");
                    hasError = true;
                }
            } catch (DateTimeParseException e) {
                model.addAttribute("errorStartDate", "Định dạng ngày không hợp lệ");
                model.addAttribute("errorEndDate", "Định dạng ngày không hợp lệ");
                hasError = true;
            }
        }

        if (hasError){
            model.addAttribute("selectedReportType", filter.getReportType());
            return "DashBoard/statistical-report";
        }
        else{
            normalizeFilterDates(filter);
        }

        // Phân biệt hành động
        if ("export".equals(action)) {
            // Gọi hàm xuất báo cáo (Excel hoặc PDF)
            switch (filter.getReportType()) {
                case "revenue" -> reportService.exportRevenueReportToExcel(filter, response);
                case "products" -> reportService.exportBestSellersReportToExcel(filter, response);
                case "customers" -> reportService.exportCustomersReportToExcel(filter, response);
            }
            return null; // Vì đã trả về file nên không cần trả view
        }

        // Ngược lại, là lọc dữ liệu
        switch (filter.getReportType()) {
            case "revenue" -> populateRevenueReport(filter, model);
            case "products" -> {
                populateBestsellerReport(filter, model);
                // Đảm bảo các biến doanh thu là object rỗng để JS không lỗi
                model.addAttribute("revenueByDay", Map.of());
                model.addAttribute("paymentMethod", Map.of());
                model.addAttribute("revenueByCategory", Map.of());
                model.addAttribute("revenueComparison", Map.of());
            }
            case "customers" -> {
                populateCustomerReport(filter, model);
                model.addAttribute("revenueByDay", Map.of());
                model.addAttribute("paymentMethod", Map.of());
                model.addAttribute("revenueByCategory", Map.of());
                model.addAttribute("revenueComparison", Map.of());
                model.addAttribute("bestSellingProducts", Map.of());
            }
            default -> model.addAttribute("error", "Loại báo cáo không hợp lệ");
        }

        // Chuyển đổi định dạng từ dd/MM/yyyy sang yyyy-MM-dd để hiển thị lại ngày trên giao diện sau khi đã thống kê
        String formatterStartDate = LocalDate.parse(filter.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
        String formatterEndDate = LocalDate.parse(filter.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
        filter.setStartDate(formatterStartDate);
        filter.setEndDate(formatterEndDate);

        model.addAttribute("selectedReportType", filter.getReportType());
        return "DashBoard/statistical-report";
    }

    // Báo cáo khách hàng
    private void populateCustomerReport(ReportFilterRequest filter, Model model) {
        int totalCustomers = reportService.countActiveCustomersCreatedBefore(filter.getEndDate());
        int totalNewCustomers = reportService.countNewCustomersBetween(filter.getStartDate(), filter.getEndDate());
        String calculateAverageOrdersPerCustomer = reportService.calculateAverageOrdersPerCustomer(filter.getStartDate(), filter.getEndDate());
        List<TopCustomerSpendingResponse> topCustomers = reportService.findTop10SpendingCustomersWithOrderCount(filter.getStartDate(), filter.getEndDate());
        Map<String, Integer> classifyCustomersBySpending = reportService.classifyCustomersBySpending(filter.getStartDate(), filter.getEndDate());

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalNewCustomers", totalNewCustomers);
        model.addAttribute("calculateAverageOrdersPerCustomer", calculateAverageOrdersPerCustomer);
        model.addAttribute("topCustomers", topCustomers);
        model.addAttribute("classifyCustomersBySpending", classifyCustomersBySpending);
    }

    // Báo cáo sản phẩm
    private void populateBestsellerReport(ReportFilterRequest filter, Model model) {
        List<BestSellingProductResponse> bestSellingProducts = reportService.getBestSellingProductsCurrentMonth(filter.getStartDate(), filter.getEndDate());
        List<BestSellingProductResponse> lowSellingProducts = reportService.getLowSellingProducts(filter.getStartDate(), filter.getEndDate());

        model.addAttribute("bestSellingProducts", bestSellingProducts);
        model.addAttribute("lowSellingProducts", lowSellingProducts);
    }

    // Báo cáo doanh thu
    private void populateRevenueReport(ReportFilterRequest filter, Model model) {

        Long totalRevenue = reportService.calculateTotalRevenue(filter.getStartDate(), filter.getEndDate());
        int totalOrders = reportService.countByDeliveryTimeEndBetween(filter.getStartDate(), filter.getEndDate());
        Long avgOrder = reportService.calculateAverageOrderAmount(filter.getStartDate(), filter.getEndDate());
        int canceled = reportService.countByCanceledOrderStatusAndDeliveryTimeEndBetween(filter.getStartDate(), filter.getEndDate());

        Map<String, Long> revenueByDay = reportService.getDailyRevenue(filter.getStartDate(), filter.getEndDate());
        Map<String, Integer> paymentMethod = reportService.countOrdersByPaymentMethod(filter.getStartDate(), filter.getEndDate());
        Map<String, Long> revenueByCategory = reportService.calculateTotalRevenueByCategory(filter.getStartDate(), filter.getEndDate());
        Map<String, Long> revenueComparison = reportService.calculateTotalRevenueForPreviousAndCurrentPeriod(filter.getStartDate(), filter.getEndDate());

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("avgOrder", avgOrder);
        model.addAttribute("canceledOrders", canceled);
        model.addAttribute("revenueByDay", revenueByDay);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("revenueByCategory", revenueByCategory);
        model.addAttribute("revenueComparison", revenueComparison);
    }

    // chuyển đổi định dạng từ yyyy-mm-dd trên html thành dd/mm/yyyy có kiểu String
    private void normalizeFilterDates(ReportFilterRequest filter) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // từ input type="date"
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // chuyển sang định dạng ngày trong db

        if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
            LocalDate start = LocalDate.parse(filter.getStartDate(), inputFormat);
            filter.setStartDate(start.format(outputFormat));
        }

        if (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) {
            LocalDate end = LocalDate.parse(filter.getEndDate(), inputFormat);
            filter.setEndDate(end.format(outputFormat));
        }
    }

}
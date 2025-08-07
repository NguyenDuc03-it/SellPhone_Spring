package com.example.SellPhone.Controller.Management;


import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.Product.RecentlySoldProductsResponse;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Entity.OrderItem;
import com.example.SellPhone.Service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/management/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

    DashboardService dashboardService;

    @GetMapping
    public String managementDashboard(Model model) {
        long totalStaff = dashboardService.countStaff();
        long totalCustomers = dashboardService.countCustomers();
        long totalProducts = dashboardService.countProducts();
        long totalPendingOrders = dashboardService.countPendingOrders();
        long totalProcessingOrders = dashboardService.countProcessingOrders();
        Long monthlyRevenue = dashboardService.calculateMonthlyRevenue();
        Map<String, Long> revenueByMonth = dashboardService.getLast6MonthsRevenue();
        Map<String, Long> importCostByMonth = dashboardService.getLast6MonthsImportCost();
        Map<String, Integer> countOrdersByStatus = dashboardService.countOrdersByStatus();
        List<Order> get10LatestOrders = dashboardService.get10LatestOrders();
        List<BestSellingProductResponse> getBestSellingProductsCurrentMonth = dashboardService.getBestSellingProductsCurrentMonth();
        List<RecentlySoldProductsResponse> getRecentlySoldProducts = dashboardService.getRecentlySoldProducts();

        // Thêm các thông tin vào model để hiển thị trên trang dashboard
        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalPendingOrders", totalPendingOrders);
        model.addAttribute("totalProcessingOrders", totalProcessingOrders);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("importCostByMonth", importCostByMonth);
        model.addAttribute("countOrdersByStatus", countOrdersByStatus);
        model.addAttribute("get10LatestOrders", get10LatestOrders);
        model.addAttribute("getBestSellingProductsCurrentMonth", getBestSellingProductsCurrentMonth);
        model.addAttribute("getRecentlySoldProducts", getRecentlySoldProducts);

        model.addAttribute("currentPage", "dashboard");
        return "DashBoard/dashboard";  // Trả về trang dashboard của admin
    }
}

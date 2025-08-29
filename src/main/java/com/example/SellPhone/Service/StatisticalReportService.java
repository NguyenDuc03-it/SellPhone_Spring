package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.StatisticalReport.TopCustomerSpendingResponse;
import com.example.SellPhone.Repository.OrderItemRepository;
import com.example.SellPhone.Repository.OrderRepository;
import com.example.SellPhone.Repository.ProductRepository;
import com.example.SellPhone.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

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
}

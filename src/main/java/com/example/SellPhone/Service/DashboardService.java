package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.Product.RecentlySoldProductsResponse;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    UserRepository userRepository;
    ProductRepository productRepository;
    OrderRepository orderRepository;
    SpecificationVariantRepository specificationVariantRepository;
    OrderItemRepository orderItemRepository;

    // Đếm tổng số lượng nhân viên
    public int countStaff() {
        return userRepository.countByRole("Nhân viên");
    }

    // Đếm tổng số lượng khách hàng
    public int countCustomers() {
        return userRepository.countByRole("Khách hàng");
    }

    // Đếm tổng số lượng sản phẩm
    public long countProducts() {
        return productRepository.count();
    }

    // Đếm số lượng đơn hàng đang chờ xử lý
    public int countPendingOrders() {
        return orderRepository.countByOrderStatus("Đang chờ xử lý");
    }

    // Đếm số lượng đơn hàng đang xử lý
    public int countProcessingOrders() {
        return orderRepository.countByOrderStatus("Đang xử lý");
    }

    // Tính tổng lợi nhuận tháng này
    public Long calculateMonthlyRevenue() {
        long totalRevenue = orderRepository.calculateTotalorderAmount();
        long totalImportCost = orderRepository.calculateTotalImportCostProductsInOrder();

        if (totalRevenue == 0 || totalImportCost == 0) {
            System.out.println("Không thể tính lợi nhuận: Một trong hai giá trị là null");
            return null;
        }

        return totalRevenue - totalImportCost;
    }

    // Tính tổng giá trị từ các đơn hàng có trạng thái đã hoàn thành trong 6 tháng gần nhất
    public Map<String, Long> getLast6MonthsRevenue() {
        List<Object[]> revenueList = orderRepository.getLast6MonthsRevenue();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : revenueList) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    // Tính tổng chi phí nhập hàng từ các đơn hàng có trạng thái đã hoàn thành trong 6 tháng gần nhất
    public Map<String, Long> getLast6MonthsImportCost() {
        List<Object[]> costList = orderRepository.getLast6MonthsImportCost();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : costList) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    // Đếm số lượng đơn hàng theo trạng thái
    public Map<String, Integer> countOrdersByStatus() {
        Map<String, Integer> orderCounts = new LinkedHashMap<>();
        orderCounts.put("Đang chờ thanh toán", orderRepository.countByOrderStatus("Đang chờ thanh toán"));
        orderCounts.put("Đang chờ xử lý", countPendingOrders());
        orderCounts.put("Đang xử lý", countProcessingOrders());
        orderCounts.put("Đang giao", orderRepository.countByOrderStatus("Đang giao hàng"));
        orderCounts.put("Đã hoàn thành", orderRepository.countByOrderStatus("Đã hoàn thành"));
        orderCounts.put("Đã hủy", orderRepository.countByOrderStatus("Đã hủy"));
        return orderCounts;
    }

    // Lấy 10 đơn hàng mới nhất
    public List<Order> get10LatestOrders() {
        return orderRepository.get10LatestOrders();
    }

    // Lấy danh sách sản phẩm bán chạy nhất trong tháng hiện tại
    public List<BestSellingProductResponse> getBestSellingProductsCurrentMonth() {
        return orderItemRepository.getBestSellingProductsCurrentMonth();
    }

    // Lấy danh sách sản phẩm đã bán gần đây
    public List<RecentlySoldProductsResponse> getRecentlySoldProducts() {
        return orderItemRepository.getRecentlySoldProducts();
    }
}

package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.Order.OrderUpdateRequest;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Kiểm tra xem khách hàng có đơn hàng nào chưa 'Đã giao' hoặc 'Đã hủy'
    public boolean hasPendingOrders(Long userId) {
        // Tìm các đơn hàng của khách hàng với trạng thái khác 'Đã giao' hoặc 'Đã hủy'
        List<Order> orders = orderRepository.findByUser_UserIdAndOrderStatusNotIn(userId, Arrays.asList("Đã giao", "Đã hủy"));
        return !orders.isEmpty(); // Nếu có đơn hàng nào thỏa mãn, trả về true
    }

    // Tìm kiếm đơn hàng theo searchQuery
    public Page<Order> searchOrder(String searchQuery, Pageable pageable) {
        return orderRepository.searchOrders(searchQuery, pageable);

    }

    // Hiển thị danh sách đơn hàng
    public Page<Order> getOrder(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    // Lọc đơn hàng theo dữ liệu tìm kiếm và trạng thái đơn hàng
    public Page<Order> searchAndFilterOrders(String searchQuery, String statusFilter, Pageable pageable) {
        return orderRepository.searchAndFilterOrders(searchQuery, statusFilter, pageable);
    }

    // Lọc đơn hàng theo trạng thái
    public Page<Order> filterByStatus(String statusFilter, Pageable pageable) {
        return orderRepository.filterByStatus(statusFilter, pageable);
    }

    // Tìm kiếm đơn hàng
    public Optional<Order> findById(Long id) {
         return orderRepository.findById(id);
    }

    // Cập nhật trạng thái đơn hàng
    @Transactional
    public void updateOrderStatus(@Valid OrderUpdateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setOrderStatus(request.getStatus());
        orderRepository.save(order);
    }

    // Hủy đơn hàng
    @Transactional
    public void cancelOrder(Order order) {
        order.setOrderStatus("Đã hủy");
        orderRepository.save(order);
    }

    // Đếm các đơn hàng có trạng thái khác 'Đã hoàn thành' và 'Đã hủy'
    public int countPendingOrders() {
        List<String> excludedStatuses = List.of("Đã hoàn thành", "Đã hủy");
        return orderRepository.countByOrderStatusNotIn(excludedStatuses);
    }
}

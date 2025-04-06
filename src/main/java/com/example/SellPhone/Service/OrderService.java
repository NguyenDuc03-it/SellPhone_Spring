package com.example.SellPhone.Service;

import com.example.SellPhone.Model.Order;
import com.example.SellPhone.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Kiểm tra xem khách hàng có đơn hàng nào chưa 'Đã giao' hoặc 'Đã hủy'
    public boolean hasPendingOrders(Long userId) {
        // Tìm các đơn hàng của khách hàng với trạng thái khác 'Đã giao' hoặc 'Đã hủy'
        List<Order> orders = orderRepository.findByUserIdAndOrderStatusNotIn(userId, Arrays.asList("Đã giao", "Đã hủy"));
        return !orders.isEmpty(); // Nếu có đơn hàng nào thỏa mãn, trả về true
    }
}

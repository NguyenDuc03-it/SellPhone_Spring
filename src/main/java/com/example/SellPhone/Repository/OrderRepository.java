package com.example.SellPhone.Repository;

import com.example.SellPhone.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndOrderStatusNotIn(Long userId, List<String> list);
}

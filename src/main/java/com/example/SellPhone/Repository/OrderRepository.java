package com.example.SellPhone.Repository;

import com.example.SellPhone.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UserIdAndOrderStatusNotIn(Long userId, List<String> list);

    @Query("""
    SELECT o FROM Order o
    JOIN o.user u
    WHERE
        CAST(o.orderId AS string) LIKE %:keyword% OR
        LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        u.phone LIKE CONCAT('%', :keyword, '%') OR
        LOWER(o.deliveryAdress) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(o.orderTime) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        CAST(o.totalPrice AS string) LIKE CONCAT('%', :keyword, '%') OR
        LOWER(o.paymentMethod) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
           SELECT o FROM Order o
           JOIN o.user u
           WHERE (:searchQuery IS NULL OR
                 CAST(o.orderId AS string) LIKE %:searchQuery% OR
                 LOWER(u.fullname) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR
                 LOWER(o.deliveryAdress) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR
                 u.phone LIKE CONCAT('%', :searchQuery, '%') OR
                 CAST(o.totalPrice AS string) LIKE CONCAT('%', :searchQuery, '%') OR
                 LOWER(o.paymentMethod) LIKE LOWER(CONCAT('%', :searchQuery, '%')))
             AND (:statusFilter IS NULL OR o.orderStatus = :statusFilter)
           """)
    Page<Order> searchAndFilterOrders(@Param("searchQuery") String searchQuery,
                                      @Param("statusFilter") String statusFilter,
                                      Pageable pageable);

    @Query("""
           SELECT o FROM Order o
           JOIN o.user u
           WHERE (o.orderStatus = :statusFilter)
           """)
    Page<Order> filterByStatus(@Param("statusFilter") String statusFilter,
                                      Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus NOT IN (:statuses)")
    int countByOrderStatusNotIn(@Param("statuses") List<String> statuses);
}

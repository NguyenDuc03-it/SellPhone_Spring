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

    int countByOrderStatus(String status);

    // Tính tổng doanh thu tháng này với trạng thái "hoàn thành"
    @Query(value = """
        SELECT SUM(total_price)
        FROM orders
        WHERE order_status = 'Đã hoàn thành'
          AND order_time IS NOT NULL
          AND MONTH(STR_TO_DATE(order_time, '%d/%m/%Y')) = MONTH(CURRENT_DATE)
          AND YEAR(STR_TO_DATE(order_time, '%d/%m/%Y')) = YEAR(CURRENT_DATE)
        """, nativeQuery = true)
    long calculateTotalorderAmount();

    @Query(value = """
        SELECT COALESCE(SUM(sv.import_price * oi.quantity), 0)
        FROM orders o
        JOIN order_items oi ON o.order_id = oi.order_id
        JOIN products p ON oi.product_id = p.product_id
        JOIN specifications s ON p.specification_id = s.specification_id
        JOIN specification_variants sv 
            ON sv.specification_id = s.specification_id 
            AND sv.rom = oi.rom
        WHERE o.order_status = 'Đã hoàn thành'
          AND o.order_time IS NOT NULL
          AND MONTH(STR_TO_DATE(o.order_time, '%d/%m/%Y')) = MONTH(CURRENT_DATE)
          AND YEAR(STR_TO_DATE(o.order_time, '%d/%m/%Y')) = YEAR(CURRENT_DATE)
        """, nativeQuery = true)
    long calculateTotalImportCostProductsInOrder();

    @Query(value = """
    SELECT
        DATE_FORMAT(STR_TO_DATE(order_time, '%d/%m/%Y'), '%m/%Y') AS monthYear,
        COALESCE(SUM(total_price), 0) AS totalRevenue
    FROM orders
    WHERE order_status = 'Đã hoàn thành'
      AND order_time IS NOT NULL
      AND STR_TO_DATE(order_time, '%d/%m/%Y') >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    GROUP BY monthYear
    ORDER BY STR_TO_DATE(CONCAT('01/', monthYear), '%d/%m/%Y')
    """, nativeQuery = true)
    List<Object[]> getLast6MonthsRevenue();

    @Query(value = """
    SELECT
        DATE_FORMAT(STR_TO_DATE(o.order_time, '%d/%m/%Y'), '%m/%Y') AS monthYear,
        COALESCE(SUM(sv.import_price * oi.quantity), 0) AS totalImportCost
    FROM orders o
    JOIN order_items oi ON o.order_id = oi.order_id
    JOIN products p ON oi.product_id = p.product_id
    JOIN specifications s ON p.specification_id = s.specification_id
    JOIN specification_variants sv
        ON sv.specification_id = s.specification_id
        AND sv.rom = oi.rom
    WHERE o.order_status = 'Đã hoàn thành'
      AND o.order_time IS NOT NULL
      AND STR_TO_DATE(o.order_time, '%d/%m/%Y') >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    GROUP BY monthYear
    ORDER BY STR_TO_DATE(CONCAT('01/', monthYear), '%d/%m/%Y')
    """, nativeQuery = true)
    List<Object[]> getLast6MonthsImportCost();

    @Query(value = """
        SELECT * FROM orders ORDER BY order_time DESC LIMIT 10
    """, nativeQuery = true)
    List<Order> get10LatestOrders();
}

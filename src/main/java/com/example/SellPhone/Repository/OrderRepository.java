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

    // Đếm số lượng đơn hàng theo trạng thái
    int countByOrderStatus(String status);

    // Tính tổng doanh thu tháng này với trạng thái "hoàn thành"
    @Query(value = """
        SELECT COALESCE(SUM(total_price), 0)
        FROM orders
        WHERE order_status = 'Đã hoàn thành'
          AND order_time IS NOT NULL
          AND MONTH(STR_TO_DATE(order_time, '%d/%m/%Y')) = MONTH(CURRENT_DATE)
          AND YEAR(STR_TO_DATE(order_time, '%d/%m/%Y')) = YEAR(CURRENT_DATE)
        """, nativeQuery = true)
    long calculateTotalorderAmount();

    // Tính tổng chi phí nhập hàng trong tháng này với trạng thái "hoàn thành"
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

    // Lấy doanh thu trong 6 tháng gần đây
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

    // Tính tổng chi phí nhập hàng trong 6 tháng gần đây
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

    // Lấy 10 đơn hàng mới nhất
    @Query(value = """
        SELECT * FROM orders ORDER BY order_time DESC LIMIT 10
    """, nativeQuery = true)
    List<Order> get10LatestOrders();

    // Tính tổng doanh thu trong khoảng thời gian từ startDate đến endDate
    @Query(value = """
            SELECT SUM(o.total_price)
            FROM orders o
            WHERE o.order_status = 'Đã hoàn thành'
            AND o.delivery_time_end IS NOT NULL
            AND STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y')
            AND STR_TO_DATE(:endDate, '%d/%m/%Y')""", nativeQuery = true)
    Long calculateTotalRevenue(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // Đếm số lượng đơn hàng theo trạng thái trong khoảng thời gian từ startDate đến endDate
    @Query(value = """
        SELECT COUNT(*)
        FROM orders o
        WHERE o.order_status = :status
        AND o.delivery_time_end IS NOT NULL
        AND STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y')
        AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        """, nativeQuery = true)
    int countByOrderStatusAndDeliveryTimeEndBetween(@Param("status") String status, @Param("startDate") String startDate, @Param("endDate") String endDate);

    // Đếm số lượng đơn hàng trong khoảng thời gian từ startDate đến endDate
    @Query(value = """
        SELECT COUNT(*)
        FROM orders o
        WHERE o.delivery_time_end IS NOT NULL
        AND STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y')
        AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        """, nativeQuery = true)
    int countByDeliveryTimeEndBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // Tính trung bình giá trị đơn hàng trong khoảng thời gian từ startDate đến endDate
    // Avg trả ra giá trị số thực FLOAT hoặc DECIMAL mà phương thức đang muốn nhận về giá trị kiểu LONG
    @Query(value = """
        SELECT CAST(AVG(o.total_price) AS UNSIGNED)
        FROM orders o
        WHERE o.order_status = 'Đã hoàn thành'
        AND o.delivery_time_end IS NOT NULL
        AND STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y')
        AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        """, nativeQuery = true)
    Long calculateAverageOrderAmount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // Đếm số lượng đơn hàng theo phương thức thanh toán
    @Query(value = """
        SELECT payment_method AS paymentMethod, COUNT(*) AS total
        FROM orders
        WHERE STR_TO_DATE(order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        GROUP BY payment_method
    """, nativeQuery = true)
    List<Object[]> countOrdersByPaymentMethod(String startDate, String endDate);

    // Lấy doanh thu hàng ngày trong khoảng thời gian từ startDate đến endDate
    @Query(value = """
        SELECT DATE(STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y')) AS deliveryDate,
                SUM(o.total_price) AS totalRevenue
        FROM orders AS o
        WHERE o.order_status = 'Đã hoàn thành'
              AND o.delivery_time_end IS NOT NULL
              AND DATE(STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y'))
              BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y')
              AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        GROUP BY DATE(STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y'))
        ORDER BY deliveryDate;
    """, nativeQuery = true)
    List<Object[]> getDailyRevenue(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // Đơn hàng trung bình trên khách hàng (đang suy nghĩ loại bỏ vì có thể kết hợp 2 truy vấn là lấy tổng khách hàng và tổng đơn hàng đã có trước đó)
    @Query(value = """
    SELECT COUNT(*) * 1.0 / COUNT(DISTINCT user_id)
    FROM orders
    WHERE order_status = 'Đã hoàn thành'
      AND STR_TO_DATE(order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
    """, nativeQuery = true)
    Double getAverageOrderPerCustomerByDate(@Param("startDate") String fromDate, @Param("endDate") String toDate);
}

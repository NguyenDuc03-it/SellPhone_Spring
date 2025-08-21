package com.example.SellPhone.Repository;

import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.Product.RecentlySoldProductsResponse;
import com.example.SellPhone.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
        SELECT
            pd.product_id AS productId,
            pd.image_url AS imageUrl,
            pd.name AS name,
            pd.color AS color,
            CAST(oi.rom AS UNSIGNED) AS rom,
            CAST(spv.selling_price AS UNSIGNED) AS sellingPrice,
            CAST(SUM(oi.quantity) AS UNSIGNED) AS totalQuantity
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        JOIN products pd ON oi.product_id = pd.product_id
        JOIN specifications spe ON pd.specification_id = spe.specification_id
        JOIN specification_variants spv ON spe.specification_id = spv.specification_id AND oi.rom = spv.rom
        WHERE (o.order_status = 'Đã hoàn thành' OR o.order_status = 'Đang giao hàng')
          AND MONTH(STR_TO_DATE(o.order_time, '%d/%m/%Y')) = MONTH(CURRENT_DATE)
          AND YEAR(STR_TO_DATE(o.order_time, '%d/%m/%Y')) = YEAR(CURRENT_DATE)
        GROUP BY pd.product_id, oi.rom, pd.image_url, pd.name, pd.color, oi.price
        ORDER BY totalQuantity DESC
        LIMIT 10
        """, nativeQuery = true)
    List<BestSellingProductResponse> getBestSellingProductsCurrentMonth();

    @Query(value = """
            SELECT
                pd.product_id AS productId,
                pd.name AS name,
                pd.color AS color,
                CAST(oi.rom AS UNSIGNED) AS rom,
                CAST(spv.selling_price AS UNSIGNED) AS sellingPrice,
                o.order_id,
                o.order_time
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.order_id
            JOIN products pd ON oi.product_id = pd.product_id
            JOIN specifications spe ON pd.specification_id = spe.specification_id
            JOIN specification_variants spv ON spe.specification_id = spv.specification_id AND oi.rom = spv.rom
            WHERE o.order_status = 'Đã hoàn thành'
            ORDER BY o.delivery_time_end DESC
            LIMIT 10
            """, nativeQuery = true)
    List<RecentlySoldProductsResponse> getRecentlySoldProducts();

    // Tính tổng doanh thu theo từng danh mục sản phẩm
    @Query(value = """
        SELECT c.name AS category, SUM(oi.price * oi.quantity) AS totalRevenue
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        JOIN products p ON oi.product_id = p.product_id
        JOIN categories c ON p.category_id = c.category_id
        WHERE o.order_status = 'Đã hoàn thành'
          AND STR_TO_DATE(o.order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        GROUP BY c.name
    """, nativeQuery = true)
    List<Object[]> calculateTotalRevenueByCategory(String startDate, String endDate);

    @Query(value = """
            SELECT
                      pd.product_id AS productId,
                      pd.name AS name,
                      pd.color AS color,
                      CAST(oi.rom AS UNSIGNED) AS rom,
                      CAST(spv.selling_price AS UNSIGNED) AS sellingPrice,
                      CAST(SUM(oi.quantity) AS UNSIGNED) AS totalQuantity
                  FROM order_items oi
                  JOIN orders o ON oi.order_id = o.order_id
                  JOIN products pd ON oi.product_id = pd.product_id
                  JOIN specifications spe ON pd.specification_id = spe.specification_id
                  JOIN specification_variants spv ON spe.specification_id = spv.specification_id AND oi.rom = spv.rom
                  WHERE (o.order_status = 'Đã hoàn thành' OR o.order_status = 'Đang giao hàng')
                  AND o.delivery_time_end IS NOT NULL
                  AND DATE(STR_TO_DATE(o.delivery_time_end, '%d/%m/%Y'))
                  BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
                  GROUP BY pd.product_id, pd.name, pd.color, oi.rom, spv.selling_price
                  ORDER BY totalQuantity DESC
                  LIMIT 10;
    """, nativeQuery = true)
    List<BestSellingProductResponse> getBestSellingProductsInPeriod(String startDate, String endDate);


}

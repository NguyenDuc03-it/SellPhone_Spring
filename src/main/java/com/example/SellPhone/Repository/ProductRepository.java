package com.example.SellPhone.Repository;

import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_CategoryId(Long categoryId);

    Page<Product> findByProductId(long quantity, Pageable pageable);

    Page<Product> findByNameContainingOrStatusContainingOrColorContainingOrCategory_Name(String searchQuery, String searchQuery1, String searchQuery2, String searchQuery3, Pageable pageable);

    boolean existsByNameAndColor(String name, String color);

    boolean existsByNameAndColorAndProductIdNot(String name, String color, Long productId);

    Optional<Product> findByName(String name);

    Optional<Product> findTopByNameOrderByProductIdDesc(String name);

    @Query(value = """
        SELECT * FROM (
            SELECT
                pd.product_id AS productId,
                pd.name AS name,
                pd.color AS color,
                CAST(oi.rom AS UNSIGNED) AS rom,
                CAST(spv.selling_price AS UNSIGNED) AS sellingPrice,
                SUM(oi.quantity) AS totalQuantity,
                ROW_NUMBER() OVER (ORDER BY SUM(oi.quantity) DESC) AS ranking
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
        ) ranked
        WHERE ranked.ranking > 10
        ORDER BY ranked.totalQuantity ASC
        LIMIT 10
    """, nativeQuery = true)
    List<BestSellingProductResponse> getLowSellingProductsInPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);
}

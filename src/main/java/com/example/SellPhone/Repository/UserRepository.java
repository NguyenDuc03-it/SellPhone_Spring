package com.example.SellPhone.Repository;

import com.example.SellPhone.DTO.Respone.StatisticalReport.TopCustomerSpendingResponse;
import com.example.SellPhone.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByRole(String role, Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findByCCCD(String CCCD);

    boolean existsByCCCD(String CCCD);

    boolean existsByEmail(String email);

    Optional<User> findIdByCCCD(String CCCD);

    Page<User> findByUserId(Long userId, Pageable pageable);

    // Tìm kiếm theo user_id và role là "Khách hàng"
    Page<User> findByUserIdAndRole(Long userId, String role, Pageable pageable);

    Page<User> findByFullnameContainingOrEmailContainingOrCCCDContainingOrPhoneContainingOrAddressContainingOrDobContainingOrGenderContainingOrStatusContaining(
            String fullname, String email, String CCCD, String phone, String address, String dob, String gender, String status, Pageable pageable);

    // Tìm kiếm các user có role là 'Khách hàng'
    Page<User> findByRoleAndFullnameContainingOrEmailContainingOrCCCDContainingOrPhoneContainingOrAddressContainingOrDobContainingOrGenderContainingOrStatusContaining(
            String role, String fullname, String email, String CCCD, String phone, String address, String dob, String gender, String status, Pageable pageable);

    // Tìm kiếm các user theo role cụ thể (không bao gồm id, cccd, phone vì id kiểu long nên phải tách hàm mà cccd và phone là chuỗi số nên cho vào chung hàm tìm kiếm id)
    Page<User> findByRoleAndFullnameContainingOrRoleAndEmailContainingOrRoleAndAddressContainingOrRoleAndDobContainingOrRoleAndGenderContainingOrRoleAndStatusContaining
            (String role, String searchQuery,
             String role1, String searchQuery1,
             String role2, String searchQuery2,
             String role3, String searchQuery3,
             String role4, String searchQuery4,
             String role5, String searchQuery5,
             Pageable pageable);

    // Tìm kiếm các user theo role cụ thể (bao gồm id, cccd, phone)
    Page<User> findByRoleAndUserIdOrRoleAndCCCDContainingOrRoleAndPhoneContaining(
            String role, long searchQuery,
            String role1, String searchQuery1,
            String role2, String searchQuery2,
            Pageable pageable);

    int countByRole(String role);

    // Đếm tổng số khách hàng mới trong khoảng thời gian từ startDate đến endDate
    @Query(value = """
        SELECT COUNT(*) FROM users u
        WHERE u.role = 'Khách hàng' AND u.status = 'Hoạt động'
        AND DATE(STR_TO_DATE(u.created_at, '%d/%m/%Y')) BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
    """, nativeQuery = true)
    int countCustomersByCreatedAtBetween(String startDate, String endDate);

    // Đếm số tổng số khách hàng đang hoạt động tính đến thời điểm endDate
    @Query(value = """
        SELECT COUNT(*) FROM users u
        WHERE u.role = 'Khách hàng' AND u.status = 'Hoạt động'
        AND DATE(STR_TO_DATE(u.created_at, '%d/%m/%Y')) <= STR_TO_DATE(:endDate, '%d/%m/%Y')
    """, nativeQuery = true)
    int countActiveCustomersCreatedBefore(String endDate);

    // Top 10 khách hàng chi tiêu nhiều nhất
    @Query(value = """
        SELECT u.user_id, u.fullname,
               CAST(SUM(o.total_price) AS UNSIGNED) AS total_spent,
               CAST(COUNT(o.order_id) AS UNSIGNED) AS total_orders
        FROM users u
        JOIN orders o ON u.user_id = o.user_id
        WHERE o.order_status = 'Đã hoàn thành'
          AND STR_TO_DATE(o.order_time, '%d/%m/%Y')
              BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
        GROUP BY u.user_id, u.fullname
        ORDER BY total_spent DESC
        LIMIT 10
    """, nativeQuery = true)
    List<TopCustomerSpendingResponse> findTop10SpendingCustomersWithOrderCount(String startDate, String endDate);

    // Phân loại khách hàng theo chi tiêu
    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT u.user_id, COALESCE(SUM(o.total_price), 0) AS totalSpent
            FROM users u
            LEFT JOIN orders o ON u.user_id = o.user_id
            WHERE u.role = 'Khách hàng'
                AND o.order_status = 'Đã hoàn thành'
                AND STR_TO_DATE(o.order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
            GROUP BY u.user_id
            HAVING totalSpent < 5000000
        ) AS subquery
    """, nativeQuery = true)
    int countCustomersUnder5M(String startDate, String endDate);


    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT u.user_id, COALESCE(SUM(o.total_price), 0) AS totalSpent
            FROM users u
            LEFT JOIN orders o ON u.user_id = o.user_id
            WHERE u.role = 'Khách hàng'
                AND o.order_status = 'Đã hoàn thành'
                AND STR_TO_DATE(o.order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
            GROUP BY u.user_id
            HAVING totalSpent >= 5000000 AND totalSpent < 20000000
        ) AS subquery
    """, nativeQuery = true)
    int countCustomersFrom5MTo20M(String startDate, String endDate);


    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT u.user_id, COALESCE(SUM(o.total_price), 0) AS totalSpent
            FROM users u
            LEFT JOIN orders o ON u.user_id = o.user_id
            WHERE u.role = 'Khách hàng'
                AND o.order_status = 'Đã hoàn thành'
                AND STR_TO_DATE(o.order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
            GROUP BY u.user_id
            HAVING totalSpent >= 20000000 AND totalSpent < 50000000
        ) AS subquery
    """, nativeQuery = true)
    int countCustomersFrom20MTo50M(String startDate, String endDate);


    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT u.user_id, COALESCE(SUM(o.total_price), 0) AS totalSpent
            FROM users u
            LEFT JOIN orders o ON u.user_id = o.user_id
            WHERE u.role = 'Khách hàng'
                AND o.order_status = 'Đã hoàn thành'
                AND STR_TO_DATE(o.order_time, '%d/%m/%Y') BETWEEN STR_TO_DATE(:startDate, '%d/%m/%Y') AND STR_TO_DATE(:endDate, '%d/%m/%Y')
            GROUP BY u.user_id
            HAVING totalSpent >= 50000000
        ) AS subquery
    """, nativeQuery = true)
    int countCustomersAbove50M(String startDate, String endDate);


}

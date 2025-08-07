package com.example.SellPhone.Repository;

import com.example.SellPhone.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

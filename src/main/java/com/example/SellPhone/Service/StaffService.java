package com.example.SellPhone.Service;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.DTO.Request.User.UserCreationRequest;
import com.example.SellPhone.DTO.Request.User.UserUpdateRequest;
import com.example.SellPhone.Entity.User;
import com.example.SellPhone.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lấy thông tin khách hàng theo ID
    public User getStaffById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));
    }

    // Tạo nhân viên mới
    public User createStaff(UserCreationRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setCreatedBy(getCurrentUserId());
        user.setUpdatedBy(getCurrentUserId());
        user.setRole("Nhân viên");

        // Chuyển đổi ngày sinh từ yyyy-MM-dd thành dd/MM/yyyy
        String formattedDob = convertDateFormat(request.getDob());
        user.setDob(formattedDob);

        user.setGender(request.getGender());
        user.setStatus("Hoạt động");

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    // Cập nhật thông tin nhân viên
    public User updateStaff(Long userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));

        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus());
        user.setRole(request.getRole());
        user.setUpdatedBy(getCurrentUserId());

        // Chuyển đổi ngày sinh từ yyyy-MM-dd thành dd/MM/yyyy
        String formattedDob = convertDateFormat(request.getDob());
        user.setDob(formattedDob);

        // Nếu mật khẩu trong request khác với mật khẩu hiện tại, mã hóa mật khẩu mới
        if(!user.getPassword().equals(request.getPassword())){
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }

    // Xóa nhân viên
    public void deleteStaff(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        userRepository.delete(user);
    }

    // Tìm kiếm id người dùng theo CCCD
    public Optional<User> findIdByCCCD(String CCCD) {
        return userRepository.findIdByCCCD(CCCD);
    }

    // Tìm kiếm nhân viên theo các trường fullname, email hoặc CCCD
    public Page<User> searchStaff(String searchQuery, Pageable pageable) {

        // Kiểm tra xem searchQuery có phải là một số hợp lệ không (dành cho id, cccd, phone)
        boolean isNumeric = false;

        // Kiểm tra nếu searchQuery có phải là một số hợp lệ (chỉ áp dụng cho id, cccd, phone)
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Kiểm tra nếu nó là một số nguyên dài (Long)
            try {
                Long.parseLong(searchQuery);  // Nếu chuyển thành Long được thì đây là một số
                isNumeric = true;
            } catch (NumberFormatException e) {
                // Nếu không thể chuyển đổi thành Long thì không phải số
                isNumeric = false;
            }
        }

        // Nếu searchQuery là số, tìm kiếm theo id, cccd, phone (cả String và Long)
        if (isNumeric) {
            return userRepository.findByRoleAndUserIdOrRoleAndCCCDContainingOrRoleAndPhoneContaining(
                    "Nhân viên", Long.parseLong(searchQuery),  // Tìm kiếm theo id
                    "Nhân viên", searchQuery,  // Tìm kiếm theo CCCD
                    "Nhân viên", searchQuery,  // Tìm kiếm theo phone
                    pageable);
        } else {
            // Nếu không phải là số, tìm kiếm chỉ theo các trường khác (fullname, email, CCCD, ...)
            return userRepository.findByRoleAndFullnameContainingOrRoleAndEmailContainingOrRoleAndAddressContainingOrRoleAndDobContainingOrRoleAndGenderContainingOrRoleAndStatusContaining(
                    "Nhân viên", searchQuery,
                    "Nhân viên", searchQuery,
                    "Nhân viên", searchQuery,
                    "Nhân viên", searchQuery,
                    "Nhân viên", searchQuery,
                    "Nhân viên", searchQuery,
                    pageable);
        }
    }

    // Hiển thị danh sách user có role là 'Nhân viên'
    public Page<User> getStaff(Pageable pageable){
        return userRepository.findByRole("Nhân viên", pageable);
    }

    // Kiểm tra email đã tồn tại hay chưa
    public boolean doesCustomerExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Kiểm tra CCCD đã tồn tại hay chưa
    public boolean doesCustomerExistByCCCD(String CCCD) {
        return userRepository.existsByCCCD(CCCD);
    }

    // Hàm chuyển đổi định dạng ngày từ yyyy-MM-dd thành dd/MM/yyyy
    private String convertDateFormat(String dob) {
        // Định dạng ngày nhận vào (yyyy-MM-dd)
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Định dạng ngày xuất ra (dd/MM/yyyy)
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate date = LocalDate.parse(dob, inputFormatter);
        return date.format(outputFormatter);
    }

    // Lấy id của người dùng đang đăng nhập
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails.getUserId();
            }
        }
        throw new RuntimeException("Không thể xác định người dùng hiện tại");
    }

    // Lấy fullname của người dùng theo id
    public String getFullnameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFullname)
                .orElse("Không xác định");
    }
}

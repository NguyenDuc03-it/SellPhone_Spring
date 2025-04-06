package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.User.UserCreationRequest;
import com.example.SellPhone.DTO.Request.User.UserUpdateRequest;
import com.example.SellPhone.Model.User;
import com.example.SellPhone.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Tạo khách hàng mới
    public User createCustomer(UserCreationRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setRole("Khách hàng");

        // Chuyển đổi ngày sinh từ yyyy-MM-dd thành dd/MM/yyyy
        String formattedDob = convertDateFormat(request.getDob());
        user.setDob(formattedDob);

        user.setGender(request.getGender());
        user.setStatus("Hoạt động");

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    // Hiển thị danh sách user có role là 'Khách hàng'
    public Page<User> getCustomer(Pageable pageable){
        return userRepository.findByRole("Khách hàng", pageable);
    }

    // Cập nhật thông tin khách hàng
    public User updateCustomer(Long userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));

        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus());
        user.setRole(request.getRole());

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

    // Lấy thông tin khách hàng theo ID
    public User getCustomerById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));
    }

    // Kiểm tra email đã tồn tại hay chưa
    public boolean doesCustomerExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Kiểm tra CCCD đã tồn tại hay chưa
    public boolean doesCustomerExistByCCCD(String CCCD) {
        return userRepository.existsByCCCD(CCCD);
    }

    // Tìm kiếm id người dùng theo CCCD
    public Optional<User> findIdByCCCD(String CCCD) {
        return userRepository.findIdByCCCD(CCCD);
    }

    // Tìm kiếm khách hàng theo các trường fullname, email hoặc CCCD
    public Page<User> searchCustomers(String searchQuery, Pageable pageable) {

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
                    "Khách hàng", Long.parseLong(searchQuery),  // Tìm kiếm theo id
                    "Khách hàng", searchQuery,  // Tìm kiếm theo CCCD
                    "Khách hàng", searchQuery,  // Tìm kiếm theo phone
                    pageable);
        } else {
            // Nếu không phải là số, tìm kiếm chỉ theo các trường khác (fullname, email, CCCD, ...)
            return userRepository.findByRoleAndFullnameContainingOrRoleAndEmailContainingOrRoleAndAddressContainingOrRoleAndDobContainingOrRoleAndGenderContainingOrRoleAndStatusContaining(
                    "Khách hàng", searchQuery,
                    "Khách hàng", searchQuery,
                    "Khách hàng", searchQuery,
                    "Khách hàng", searchQuery,
                    "Khách hàng", searchQuery,
                    "Khách hàng", searchQuery,
                    pageable);
        }
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

    // Xóa khách hàng
    public void deleteCustomer(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        userRepository.delete(user);
    }
}

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

        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus());

        return userRepository.save(user);
    }

    // Lấy thông tin khách hàng theo ID
    public User getCustomerById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));
    }

    // Lấy thông tin khách hàng theo email
    public boolean doesCustomerExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Lấy thông tin khách hàng theo CCCD
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

}

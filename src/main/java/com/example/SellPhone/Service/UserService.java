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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public Page<User> getUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }


    public User updateUser(Long userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Không tìm thấy người dùng"));

        user.setPassword(request.getPassword());
        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone());
        user.setCCCD(request.getCCCD());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus());

        return userRepository.save(user);
    }
}

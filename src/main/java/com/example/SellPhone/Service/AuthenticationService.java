package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.Auth.AuthenticationRequest;
import com.example.SellPhone.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

// Xác thực và phân quyền tài khoản
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    AuthenticationManager authenticationManager;

    public boolean authenticate(AuthenticationRequest request){
        try {
            // Tạo một đối tượng Authentication với email và mật khẩu
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            // Xác thực người dùng
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Nếu xác thực thành công, trả về true
            return authentication.isAuthenticated();
        } catch (BadCredentialsException e) {
            return false;
        }
    }
}

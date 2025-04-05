package com.example.SellPhone.Controller.Account;

import com.example.SellPhone.Config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("username")
    public String addUserToModel() {
        // Lấy tên người dùng từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails.getFullname(); // Trả về fullname của người dùng
            }
        }
        return null; // Trường hợp không có người dùng nào đăng nhập
    }
}

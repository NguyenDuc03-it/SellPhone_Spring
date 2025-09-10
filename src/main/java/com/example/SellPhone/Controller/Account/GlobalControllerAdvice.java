package com.example.SellPhone.Controller.Account;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.Service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ShoppingCartService shoppingCartService;

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

    // Đếm số lượng order item trong giỏ hàng
    @ModelAttribute("cartItemCount")
    public Integer addCartItemCountToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                Long userId = userDetails.getUserId();
                return shoppingCartService.countTotalCartItems(userId);
            }
        }
        return 0; // Nếu chưa đăng nhập
    }
}

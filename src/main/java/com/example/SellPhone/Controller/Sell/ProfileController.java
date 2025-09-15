package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.Service.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    CustomerService customerService;

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/user/profile"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }
        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        model.addAttribute("user", customerService.getCustomerById(userId));
        return "Sell/profile";
    }



}

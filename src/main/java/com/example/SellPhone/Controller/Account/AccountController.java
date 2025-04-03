package com.example.SellPhone.Controller.Account;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class AccountController {

    // Trang chủ
    @GetMapping("/")
    public String index(Model model){ return "Sell/index";}

    // Mở trang đăng nhập
    @GetMapping("/login")
    public String login_(Model model){ return "AboutAccount/login";}

    // Mở trang đăng ký
    @GetMapping("/register")
    public String register_(Model model){ return "AboutAccount/register";}

}

package com.example.SellPhone.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class UserController {
    @GetMapping("/hello")
    String sayHello(){
        return "Hello";
    }

    @RequestMapping("/")
    public String index(Model model){
        return "AboutAccount/login";
    }
}

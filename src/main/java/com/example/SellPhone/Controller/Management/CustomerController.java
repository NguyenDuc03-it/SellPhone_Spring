package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Request.User.UserCreationRequest;
import com.example.SellPhone.Model.User;
import com.example.SellPhone.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


// Trang quản lý khách hàng
@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    User addCustomer(@RequestBody UserCreationRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    String customerManagement(Model model, @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 1);  // 10 dòng trên mỗi trang

        Page<User> customers =userService.getUsers(pageable);
        model.addAttribute("customers", customers);
        return "DashBoard/quanLyKhachHang";
    }
}

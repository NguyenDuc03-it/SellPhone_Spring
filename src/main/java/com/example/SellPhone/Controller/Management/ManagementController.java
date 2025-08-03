package com.example.SellPhone.Controller.Management;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
public class ManagementController {

    @GetMapping("/dashboard")
    public String managementDashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "DashBoard/dashboard";  // Trả về trang dashboard của admin
    }
}

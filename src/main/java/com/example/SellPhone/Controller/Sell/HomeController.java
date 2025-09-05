package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.Product.ProductSummaryRespone;
import com.example.SellPhone.Service.DashboardService;
import com.example.SellPhone.Service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeController {

    ProductService productService;
    DashboardService service;

    @GetMapping("/home")
    public String userHome(Model model) {
        List<BestSellingProductResponse> bestSellingProducts = service.getBestSellingProductsCurrentMonth();
        List<ProductSummaryRespone> products = productService.getProductSummary();
        model.addAttribute("bestSellingProducts", bestSellingProducts);
        model.addAttribute("productSummary", products);
        return "Sell/index";  // Trang chủ
    }
}

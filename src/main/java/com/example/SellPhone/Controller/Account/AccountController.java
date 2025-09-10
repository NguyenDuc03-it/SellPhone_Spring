package com.example.SellPhone.Controller.Account;


import com.example.SellPhone.DTO.Respone.Product.BestSellingProductResponse;
import com.example.SellPhone.DTO.Respone.Product.ProductSummaryRespone;
import com.example.SellPhone.Service.DashboardService;
import com.example.SellPhone.Service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {

    ProductService productService;
    DashboardService service;

    // Trang chủ
    @GetMapping("/")
    public String index(Model model){
        Pageable firstPage = PageRequest.of(0, 15);
        Page<ProductSummaryRespone> firstProducts = productService.getProductSummary(firstPage);

        List<BestSellingProductResponse> bestSelling = service.getBestSellingProductsCurrentMonth()
                .stream()
                .limit(6)
                .toList();

        model.addAttribute("productSummary", firstProducts.getContent());
        model.addAttribute("bestSellingProducts", bestSelling);
        model.addAttribute("hasMore", firstProducts.hasNext()); // Optional: dùng để hiển thị nút "Xem thêm"

        return "Sell/index";
    }

    // Hiển thị thêm sản phẩm
    @GetMapping("/more-products")
    public ResponseEntity<Page<ProductSummaryRespone>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryRespone> pageResult = productService.getProductSummary(pageable);
        return ResponseEntity.ok(pageResult);
    }

    // Mở trang đăng nhập
    @GetMapping("/login")
    public String login_(Model model){ return "AboutAccount/login";}

    // Mở trang đăng ký
    @GetMapping("/register")
    public String register_(Model model){ return "AboutAccount/register";}

    // Mở trang quên mật khẩu
    @GetMapping("/forgot-password")
    public String forgotPassword_(Model model){ return "AboutAccount/forgot-password";}
}

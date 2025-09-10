package com.example.SellPhone.Controller.Sell;

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
@RequiredArgsConstructor
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeController {

    ProductService productService;
    DashboardService service;

    @GetMapping("/home")
    public String userHome(Model model) {
        Pageable firstPage = PageRequest.of(0, 15);
        Page<ProductSummaryRespone> firstProducts = productService.getProductSummary(firstPage);

        List<BestSellingProductResponse> bestSelling = service.getBestSellingProductsCurrentMonth()
                .stream()
                .limit(6)
                .toList();

        model.addAttribute("productSummary", firstProducts.getContent());
        model.addAttribute("bestSellingProducts", bestSelling);
        model.addAttribute("hasMore", firstProducts.hasNext()); // Optional: dùng để hiển thị nút "Xem thêm"

        return "Sell/index";  // Trang chủ
    }

    // hiển thị thêm sản phẩm
    @GetMapping("/more-products")
    public ResponseEntity<Page<ProductSummaryRespone>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryRespone> pageResult = productService.getProductSummary(pageable);
        return ResponseEntity.ok(pageResult);
    }
}

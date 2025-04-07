package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.Model.Product;
import com.example.SellPhone.Model.User;
import com.example.SellPhone.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/management/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Hiển thị danh sách sản phẩm và phân trang
    @GetMapping
    String productManagement(Model model, @RequestParam(required = false) String searchQuery, @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang
        Page<Product> products;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Nếu có tìm kiếm
            products = productService.searchProduct(searchQuery, pageable);
            model.addAttribute("searchQuery", searchQuery); // Lưu giá trị tìm kiếm để giữ lại khi chuyển trang
        } else {
            // Nếu không có tìm kiếm
            products = productService.getProduct(pageable);
        }

        // Truyền các URL vào model
        model.addAttribute("products", products);

        return "DashBoard/quanLySanPham";
    }

    //Mở trang thêm sản phẩm
    @GetMapping("/op_add")
    String opAddProduct(Model model){
        model.addAttribute("product", new Product());
        return "DashBoard/themSanPham";
    }

    //Mở trang cập nhật thông tin sản phẩm
    @PostMapping("/op_update")
    String opUpdateProduct(@RequestParam Long productId, Model model){
        Product product = productService.getProductById(productId);

        model.addAttribute("product", product);
        return "DashBoard/suaSanPham";
    }

}

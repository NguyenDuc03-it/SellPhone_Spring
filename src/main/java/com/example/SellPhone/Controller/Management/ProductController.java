package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.DTO.Request.Product.ProductCreationRequest;
import com.example.SellPhone.Model.Product;
import com.example.SellPhone.Service.CategoryService;
import com.example.SellPhone.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/management/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

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
        model.addAttribute("request", new ProductCreationRequest());
        model.addAttribute("categories", categoryService.findAll()); // Trả về List<Category>
        return  "DashBoard/themSanPham";
    }

    //Mở trang cập nhật thông tin sản phẩm
    @PostMapping("/op_update")
    String opUpdateProduct(@RequestParam Long productId, Model model){
        Product product = productService.getProductById(productId);

        model.addAttribute("product", product);
        return "DashBoard/suaSanPham";
    }

    // Chức năng thêm sản phẩm
    @PostMapping("/add")
    String addCustomer(@Valid @ModelAttribute("request") ProductCreationRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){

        if (request.getImageUrl() == null || request.getImageUrl().isEmpty()) {
            bindingResult.rejectValue("imageUrl", "error.imageUrl", "Vui lòng chọn hình ảnh");
        }

        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            return "DashBoard/themSanPham"; // Trả về một thông báo lỗi
        }

        if(productService.doesProductExistByNameAndColor(request.getName(), request.getColor())){
            bindingResult.rejectValue("name", "error.name", "Sản phẩm với tên và màu này đã tồn tại");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            return "DashBoard/themSanPham"; // Trả về thông báo lỗi nếu
        }

        if (!categoryService.existsById(request.getCategoryId())) {
            bindingResult.rejectValue("categoryId", "error.categoryId", "Danh mục không hợp lệ");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            return "DashBoard/themSanPham";
        }

        if(request.getSpecification() == null) {
            bindingResult.rejectValue("specificationIds", "error.specificationIds", "Vui lòng chọn ít nhất một thông số kỹ thuật");
            model.addAttribute("request", request);
            model.addAttribute("categories", categoryService.findAll());
            return "DashBoard/themSanPham";
        }

        Product product = productService.createProduct(request);
        // Thêm thông báo thành công vào FlashAttributes
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        return "redirect:/management/products"; // Chuyển hướng về trang danh sách sản phẩm
    }


}

package com.example.SellPhone.Controller.Management;


import com.example.SellPhone.DTO.Request.Category.CategoryCreationRequest;
import com.example.SellPhone.DTO.Request.Category.CategoryUpdateRequest;
import com.example.SellPhone.Entity.Category;
import com.example.SellPhone.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/management/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Hiển thị danh sách danh mục và phân trang
    @GetMapping
    String categoryManagement(Model model, @RequestParam(required = false) String searchQuery, @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);  // 10 dòng trên mỗi trang
        Page<Category> categories;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Nếu có tìm kiếm
            categories = categoryService.searchCategory(searchQuery, pageable);
            model.addAttribute("searchQuery", searchQuery); // Lưu giá trị tìm kiếm để giữ lại khi chuyển trang
        } else {
            // Nếu không có tìm kiếm
            categories = categoryService.getCategory(pageable);
        }

        // Truyền các URL vào model
        model.addAttribute("categories", categories);
        model.addAttribute("category", new CategoryCreationRequest());
        model.addAttribute("updateCategory", new CategoryUpdateRequest());
        model.addAttribute("currentPage", "categories");
        return "DashBoard/category-management";
    }

    // Chức năng thêm danh mục
    @PostMapping("/add")
    String addCategory(@Valid @ModelAttribute("category") CategoryCreationRequest request, BindingResult bindingResult, Model model,
                       RedirectAttributes redirectAttributes, @RequestParam(required = false) String searchQuery,
                       @RequestParam(defaultValue = "0") int page){
        if (bindingResult.hasErrors()) {
            // Truyền lại danh mục đã có sẵn vào model để không bị lỗi phân trang
            Pageable pageable = PageRequest.of(page, 10); // Cần truyền thông tin phân trang
            Page<Category> categories;

            if (searchQuery != null && !searchQuery.isEmpty()) {
                categories = categoryService.searchCategory(searchQuery, pageable);
            } else {
                categories = categoryService.getCategory(pageable);
            }

            model.addAttribute("categories", categories);
            model.addAttribute("errorMessage", "Thêm danh mục thất bại!");
            model.addAttribute("currentPage", "categories");
            model.addAttribute("updateCategory", new CategoryUpdateRequest());
            return "DashBoard/category-management"; // Trả về một thông báo lỗi
        }

        if(categoryService.doesCategoryExistByName(request.getName())){
            bindingResult.rejectValue("name", "error.name", "Danh mục đã tồn tại");

            // Truyền lại danh mục vào model
            Pageable pageable = PageRequest.of(page, 10); // Cần truyền thông tin phân trang
            Page<Category> categories;

            if (searchQuery != null && !searchQuery.isEmpty()) {
                categories = categoryService.searchCategory(searchQuery, pageable);
            } else {
                categories = categoryService.getCategory(pageable);
            }

            model.addAttribute("categories", categories);
            model.addAttribute("errorMessage", "Danh mục đã tồn tại!");
            model.addAttribute("currentPage", "categories");
            model.addAttribute("updateCategory", new CategoryUpdateRequest());
            return "DashBoard/category-management"; // Trả về thông báo lỗi nếu email đã tồn tại
        }


        Category category = categoryService.createCategory(request);
        // Thêm thông báo thành công vào FlashAttributes
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        return "redirect:/management/categories"; // Chuyển hướng về trang danh sách khách hàng
    }

    // Chức năng sửa thông tin danh mục
    @PostMapping("/update")
    String updateCategory(@Valid @ModelAttribute("updateCategory") CategoryUpdateRequest request, BindingResult bindingResult, Model model,
                          RedirectAttributes redirectAttributes, @RequestParam(required = false) String searchQuery,
                          @RequestParam(defaultValue = "0") int page){
        if (bindingResult.hasErrors()) {
            // Truyền lại danh mục đã có sẵn vào model để không bị lỗi phân trang
            Pageable pageable = PageRequest.of(page, 10); // Cần truyền thông tin phân trang
            Page<Category> categories;

            if (searchQuery != null && !searchQuery.isEmpty()) {
                categories = categoryService.searchCategory(searchQuery, pageable);
            } else {
                categories = categoryService.getCategory(pageable);
            }

            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("Error: " + error.getDefaultMessage());
            });

            model.addAttribute("categories", categories);
            model.addAttribute("errorMessage", "Cập nhật thông tin danh mục thất bại!");
            model.addAttribute("currentPage", "categories");
            return "DashBoard/category-management"; // Trả về một thông báo lỗi
        }

        try {
            categoryService.updateCategory(request.getCategoryId(), request);
            // Thêm thông báo thành công vào FlashAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin danh mục thành công!");
            return "redirect:/management/categories"; // Chuyển hướng về trang danh sách danh mục
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/management/categories"; // Chuyển hướng về trang danh sách danh mục
        }
    }

    // Chức năng xóa danh mục
    @PostMapping("/delete")
    public String deleteCategory(@RequestParam Long categoryId, RedirectAttributes redirectAttributes) {
        if(!categoryService.existsById(categoryId)){
            redirectAttributes.addFlashAttribute("errorMessage", "Danh mục không tồn tại!");
            return "redirect:/management/categories";
        }
        try {
            categoryService.deleteCategory(categoryId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/management/categories";
    }
}

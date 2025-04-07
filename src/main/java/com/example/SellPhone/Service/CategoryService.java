package com.example.SellPhone.Service;

import com.example.SellPhone.DTO.Request.Category.CategoryCreationRequest;
import com.example.SellPhone.DTO.Request.Category.CategoryUpdateRequest;
import com.example.SellPhone.Model.Category;
import com.example.SellPhone.Model.Product;
import com.example.SellPhone.Repository.CategoryRepository;
import com.example.SellPhone.Repository.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Tìm kiếm danh mục theo tên, ghi chú hoặc trạng thái
    public Page<Category> searchCategory(String searchQuery, Pageable pageable) {
        // Kiểm tra xem searchQuery có phải là một số hợp lệ không
        boolean isNumeric = false;

        // Kiểm tra nếu searchQuery có phải là một số hợp lệ
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Kiểm tra nếu nó là một số nguyên dài (Long)
            try {
                Long.parseLong(searchQuery);  // Nếu chuyển thành Long được thì đây là một số
                isNumeric = true;
            } catch (NumberFormatException e) {
                // Nếu không thể chuyển đổi thành Long thì không phải số
                isNumeric = false;
            }
        }

        // Nếu searchQuery là số, tìm kiếm theo id
        if (isNumeric) {
            return categoryRepository.findByCategoryId(Long.parseLong(searchQuery), pageable);
        } else {
            // Nếu không phải là số, tìm kiếm chỉ theo các trường khác
            return categoryRepository.findByNameContainingOrNotesContainingOrStatusContaining(
                    searchQuery, searchQuery, searchQuery, pageable);
        }
    }

    // Lấy tất cả danh mục
    public Page<Category> getCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }


    // Kiểm tra xem danh mục có tồn tại theo name không
    public boolean doesCategoryExistByName(String name) {
        return categoryRepository.existsByName(name);
    }

    // Tạo danh mục mới
    public Category createCategory( CategoryCreationRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setNotes(request.getNotes());
        category.setStatus(request.getStatus());
        return categoryRepository.save(category);
    }

    // Cập nhật danh mục
    public Category updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        // Cập nhật các trường của danh mục
        category.setNotes(request.getNotes());
        category.setStatus(request.getStatus());

        return categoryRepository.save(category);
    }

    // Xóa danh mục
    public void deleteCategory(Long categoryId) {
        // Trước khi xóa, kiểm tra xem có thể xóa được không
        if (canDeleteCategory(categoryId)) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new IllegalStateException("Danh mục có sản phẩm không hợp lệ, không thể xóa.");
        }
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
    }

    // Kiểm tra xem danh mục có thể xóa không
    public boolean canDeleteCategory(Long categoryId) {
        // Kiểm tra xem có sản phẩm nào trong danh mục có trạng thái "Dừng bán" hoặc "Hết hàng"
        List<Product> products = productRepository.findByCategory_CategoryId(categoryId);

        for (Product product : products) {
            if (product.getStatus().equals("Dừng bán") || product.getStatus().equals("Hết hàng")) {
                return false;  // Nếu có sản phẩm không hợp lệ, không cho phép xóa danh mục
            }
        }

        return true;  // Tất cả sản phẩm đều hợp lệ
    }
}

package com.example.SellPhone.Service;

import com.example.SellPhone.Model.Product;
import com.example.SellPhone.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Tìm kiếm sản phẩm theo searchQuery
    public Page<Product> searchProduct(String searchQuery, Pageable pageable) {
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
            return productRepository.findByProductIdOrQuantity(Long.parseLong(searchQuery), Long.parseLong(searchQuery), pageable);
        } else {
            // Nếu không phải là số, tìm kiếm chỉ theo các trường khác
            return productRepository.findByNameContainingOrColorContainingOrStatusContainingOrCategory_Name(
                    searchQuery, searchQuery, searchQuery, searchQuery, pageable);
        }
    }

    // Hiển thị danh sách sản phẩm
    public Page<Product> getProduct(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Không tìm thấy sản phẩm"));
    }
}

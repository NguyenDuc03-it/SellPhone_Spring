package com.example.SellPhone.Repository;

import com.example.SellPhone.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_CategoryId(Long categoryId);

    Page<Product> findByProductIdOrQuantity(long productId, long quantity, Pageable pageable);

    Page<Product> findByNameContainingOrColorContainingOrStatusContainingOrCategory_Name(String searchQuery, String searchQuery1, String searchQuery2, String searchQuery3, Pageable pageable);

    boolean existsByNameAndColor(String name, String color);
}

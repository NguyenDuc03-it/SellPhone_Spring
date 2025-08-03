package com.example.SellPhone.Repository;

import com.example.SellPhone.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_CategoryId(Long categoryId);

    Page<Product> findByProductId(long quantity, Pageable pageable);

    Page<Product> findByNameContainingOrStatusContainingOrColorContainingOrCategory_Name(String searchQuery, String searchQuery1, String searchQuery2, String searchQuery3, Pageable pageable);

    boolean existsByNameAndColor(String name, String color);

    boolean existsByNameAndColorAndProductIdNot(String name, String color, Long productId);

    Optional<Product> findByName(String name);
}

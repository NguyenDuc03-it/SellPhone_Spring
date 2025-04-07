package com.example.SellPhone.Repository;

import com.example.SellPhone.Model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByNameContainingOrNotesContainingOrStatusContaining(String name, String notes, String status, Pageable pageable);

    Page<Category> findByCategoryId(long categoryId, Pageable pageable);

    boolean existsByName(String name);
}

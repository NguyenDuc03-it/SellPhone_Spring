package com.example.SellPhone.Repository;

import com.example.SellPhone.Entity.SpecificationVariant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationVariantRepository extends JpaRepository<SpecificationVariant, Long> {

    @Modifying
    @Transactional
    void deleteBySpecification_SpecificationId(Long specificationId);
}

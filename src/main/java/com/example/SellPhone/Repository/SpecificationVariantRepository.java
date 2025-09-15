package com.example.SellPhone.Repository;

import com.example.SellPhone.Entity.Specification;
import com.example.SellPhone.Entity.SpecificationVariant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecificationVariantRepository extends JpaRepository<SpecificationVariant, Long> {

    @Modifying
    @Transactional
    void deleteBySpecification_SpecificationId(Long specificationId);

    Optional<SpecificationVariant> findBySpecificationAndRom(Specification specification, Integer rom);

    @Query("""
            SELECT v FROM SpecificationVariant v
            JOIN v.specification s
            JOIN Product p ON p.specification = s
            WHERE p.productId = :productId AND v.rom = :rom
            """)
    Optional<SpecificationVariant> findByProductIdAndRom(
            @Param("productId") Long productId,
            @Param("rom") Integer rom);
}

package com.example.SellPhone.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "specification_variants",
uniqueConstraints = @UniqueConstraint(columnNames = {"specification_id", "rom", "color"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecificationVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specification_variant_id")
    private Long specificationVariantId;

    @Column(name = "rom")
    private Integer rom;

    @Column(name = "import_price")
    private Long importPrice;

    @Column(name = "selling_price")
    private Long sellingPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "import_date")
    private String importDate;

    @ManyToOne
    @JoinColumn(name = "specification_id")  // product_id là khóa ngoại
    private Specification specification;

    @PrePersist
    protected void onCreate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.importDate = LocalDate.now().format(formatter);
    }
}

package com.example.SellPhone.Model;

import jakarta.persistence.*;
import lombok.*;

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
    Long specificationVariantId;

    @Column(name = "rom")
    private int rom;

    @Column(name = "import_price")
    private Long importPrice;

    @Column(name = "selling_price")
    private Long sellingPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "specification_id")  // product_id là khóa ngoại
    private Specification specification;
}

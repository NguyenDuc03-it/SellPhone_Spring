package com.example.SellPhone.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")  // category_id là khóa ngoại
    private Category category;

    @Column(name = "color")
    private String color;

    @Column(name = "import_price")
    private Long importPrice;

    @Column(name = "selling_price")
    private Long sellingPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "product-specification",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "specification_id")
    )
    private List<Specification> specifications;
}

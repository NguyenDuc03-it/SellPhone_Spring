package com.example.SellPhone.Model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "status")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "specification_id")
    private Specification specification;
}

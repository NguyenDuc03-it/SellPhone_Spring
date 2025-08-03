package com.example.SellPhone.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status")
    private String status;

    // Mối quan hệ OneToMany từ Category đến Product
    @OneToMany(mappedBy = "category")  // 'category' là tên trường trong Product
    private List<Product> products;
}

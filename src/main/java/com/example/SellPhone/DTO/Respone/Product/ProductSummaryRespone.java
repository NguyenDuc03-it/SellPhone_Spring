package com.example.SellPhone.DTO.Respone.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryRespone {
    private Long productId;
    private String name;
    private String imageUrl;
    private String color;
    private String chipset;
    private String operatingSystem;
    private Long sellingPrice;
}

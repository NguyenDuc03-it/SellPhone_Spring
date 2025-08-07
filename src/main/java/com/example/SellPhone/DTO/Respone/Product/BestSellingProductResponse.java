package com.example.SellPhone.DTO.Respone.Product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BestSellingProductResponse {
    long productId;
    String imageUrl;
    String name;
    String color;
    Long rom;
    Long sellingPrice;
    Long totalQuantity;
}

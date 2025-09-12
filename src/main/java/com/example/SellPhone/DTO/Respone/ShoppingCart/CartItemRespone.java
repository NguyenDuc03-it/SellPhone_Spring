package com.example.SellPhone.DTO.Respone.ShoppingCart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRespone {
    Long cartItemId;
    Long productId;
    String name;
    String imageUrl;
    Integer rom;
    String color;
    Long sellingPrice;
    Integer quantity;
    Integer stock;
}

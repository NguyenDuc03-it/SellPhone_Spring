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
public class RecentlySoldProductsResponse {
    long productId;
    String name;
    String color;
    long rom;
    long sellingPrice;
    long orderId;
    String orderTime;
}

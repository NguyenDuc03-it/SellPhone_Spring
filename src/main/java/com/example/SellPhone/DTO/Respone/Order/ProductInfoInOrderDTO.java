package com.example.SellPhone.DTO.Respone.Order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInfoInOrderDTO {
    String name;
    String color;
    int rom;
    Long price;
    int quantity;
}

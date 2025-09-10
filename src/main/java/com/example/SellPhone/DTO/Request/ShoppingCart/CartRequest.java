package com.example.SellPhone.DTO.Request.ShoppingCart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {
    Long productId;
    int quantity;
}

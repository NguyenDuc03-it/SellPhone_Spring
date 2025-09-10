package com.example.SellPhone.DTO.Respone.Product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecificationVariantResponse {
    Integer rom;
    Long importPrice;
    Long sellingPrice;
    Integer quantity;
    String importDate;
}

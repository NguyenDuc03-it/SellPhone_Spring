package com.example.SellPhone.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecificationVariantDTO {
    Integer rom;
    Long importPrice;
    Long sellingPrice;
    Integer quantity;
}

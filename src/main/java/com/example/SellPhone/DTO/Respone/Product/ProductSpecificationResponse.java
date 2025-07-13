package com.example.SellPhone.DTO.Respone.Product;

import com.example.SellPhone.DTO.ProductSpecificationDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecificationResponse {

     String name;
     String imageUrl;
     Long sellingPrice;
     String description;
     List<ProductSpecificationDTO> specifications;
}

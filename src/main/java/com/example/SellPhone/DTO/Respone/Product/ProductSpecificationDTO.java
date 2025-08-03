package com.example.SellPhone.DTO.Respone.Product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecificationDTO {
     Float screenSize;
     String rearCamera;
     String frontCamera;
     String chipset;
     Integer ram;
     String sim;
     String operatingSystem;
     String cpu;
     String charging;

     List<ProductSpecificationVariantDTO> variants;
}

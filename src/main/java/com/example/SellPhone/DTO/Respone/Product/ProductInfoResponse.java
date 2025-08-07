package com.example.SellPhone.DTO.Respone.Product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInfoResponse {
    Float screenSizeInput;
    String operatingSystem;
    String rearCamera;
    String frontCamera;
    String sim;
    String chipset;
    String cpu;
    String charging;
    Integer ram;
    String description;
    Long categoryId;
}
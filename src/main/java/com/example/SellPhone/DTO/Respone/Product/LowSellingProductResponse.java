package com.example.SellPhone.DTO.Respone.Product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LowSellingProductResponse {
    long productId;
    String name;
    String color;
    Long rom;
    Long sellingPrice;
    Long totalQuantity;
    Integer ranking;


    public LowSellingProductResponse(Object productId, Object name, Object color, Object rom, Object sellingPrice, Object totalQuantity, Object ranking) {
        this.productId = ((Number) productId).longValue();
        this.name = (String) name;
        this.color = (String) color;
        this.rom = rom == null ? null : ((Number) rom).longValue();
        this.sellingPrice = sellingPrice == null ? null : ((Number) sellingPrice).longValue();
        this.totalQuantity = totalQuantity == null ? null : ((Number) totalQuantity).longValue();
        this.ranking = ranking == null ? null : ((Number) ranking).intValue();
    }

}
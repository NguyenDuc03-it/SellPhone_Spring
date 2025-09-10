package com.example.SellPhone.DTO.Request.CheckOut;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckOutRequest  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    Long productId;
    String productName;
    String color;
    Integer rom;
    Integer price;
    Integer quantity;
}

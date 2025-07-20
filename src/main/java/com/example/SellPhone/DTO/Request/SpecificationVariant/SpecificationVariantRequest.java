package com.example.SellPhone.DTO.Request.SpecificationVariant;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecificationVariantRequest {

    @NotNull(message = "ROM không được để trống")
    @Min(1)
    private Integer rom;

    @NotNull(message = "Giá nhập không được để trống")
    @Positive(message = "Kích thước màn hình phải lớn hơn 0")
    private Long importPrice;

    @NotNull(message = "Giá bán không được để trống")
    @Positive(message = "Kích thước màn hình phải lớn hơn 0")
    private Long sellingPrice;

    @NotNull(message = "Số lượng không được để trống")
    @Min(0)
    private Integer quantity;
}

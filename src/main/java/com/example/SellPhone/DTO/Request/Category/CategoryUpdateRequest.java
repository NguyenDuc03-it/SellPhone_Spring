package com.example.SellPhone.DTO.Request.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {

    @NotNull(message = "ID danh mục không được để trống")
    @Min(value = 1, message = "ID danh mục không hợp lệ")
    private Long categoryId;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    @NotBlank(message = "Ghi chú không được để trống")
    private String notes;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}

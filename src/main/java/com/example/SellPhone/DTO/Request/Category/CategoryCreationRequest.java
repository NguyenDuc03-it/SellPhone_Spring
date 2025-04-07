package com.example.SellPhone.DTO.Request.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreationRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    @NotBlank(message = "Ghi chú không được để trống")
    private String notes;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}

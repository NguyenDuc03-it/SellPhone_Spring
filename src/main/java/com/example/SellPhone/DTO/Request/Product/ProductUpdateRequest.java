package com.example.SellPhone.DTO.Request.Product;

import com.example.SellPhone.DTO.Request.Specification.SpecificationCreationRequest;
import com.example.SellPhone.DTO.Request.SpecificationVariant.SpecificationVariantRequest;
import com.example.SellPhone.Model.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductUpdateRequest {

    @NotNull(message = "Không tìm thấy ID sản phẩm")
    @Min(value = 1, message = "ID sản phẩm không hợp lệ")
    private Long productId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 kí tự")
    String name;


    MultipartFile imageUrl;
    String existingImageUrl;

    @NotNull(message = "Danh mục không được để trống")
    @Valid
    Category category;

    @NotBlank(message = "Màu sản phẩm không được để trống")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Màu sản phẩm chỉ được chứa chữ cái")
    @Size(max = 25, message = "Màu sản phẩm không được vượt quá 25 kí tự")
    String color;

    String description;

    @Valid
    @NotNull(message = "Thông số kỹ thuật không được để trống")
    SpecificationCreationRequest specification; // Danh sách các specificationId cho sản phẩm, tránh lưu trực tiếp đối tượng Specification

    @Valid
    @NotEmpty(message = "Cần có ít nhất một phiên bản ROM")
    private List<SpecificationVariantRequest> romVariants;
}

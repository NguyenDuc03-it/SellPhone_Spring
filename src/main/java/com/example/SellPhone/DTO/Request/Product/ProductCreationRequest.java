package com.example.SellPhone.DTO.Request.Product;

import com.example.SellPhone.DTO.Request.Specification.SpecificationCreationRequest;
import com.example.SellPhone.DTO.Request.SpecificationVariant.SpecificationVariantRequest;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 kí tự")
    String name;

    @NotNull(message = "Hình ảnh không được để trống")
    MultipartFile imageUrl;

    @NotNull(message = "Danh mục không được để trống")
    Long categoryId;  // Thay vì trực tiếp lưu category, ta chỉ lưu categoryId

    @NotBlank(message = "Màu sản phẩm không được để trống")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Màu sản phẩm chỉ được chứa chữ cái")
    @Size(max = 25, message = "Màu sản phẩm không được vượt quá 25 kí tự")
    String color;

//    @NotNull(message = "Số lượng không được để trống")
//    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
//    Integer quantity;

//    @NotBlank(message = "Trạng thái không được để trống")
//    String status;

    String description;

    @NotNull(message = "Thông số kỹ thuật không được để trống")
    SpecificationCreationRequest specification; // Danh sách các specificationId cho sản phẩm, tránh lưu trực tiếp đối tượng Specification

    @NotEmpty(message = "Cần có ít nhất một phiên bản ROM")
    private List<SpecificationVariantRequest> romVariants;
}

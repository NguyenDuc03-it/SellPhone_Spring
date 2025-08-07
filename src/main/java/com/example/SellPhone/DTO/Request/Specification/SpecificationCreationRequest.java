package com.example.SellPhone.DTO.Request.Specification;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecificationCreationRequest {

    @NotBlank(message = "Kích thước màn hình không được để trống")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Kích thước màn hình phải là số dương hợp lệ")
    private String screenSizeInput;

    @Positive(message = "Kích thước màn hình phải lớn hơn 0")
    private Float screenSize;     // Kích thước màn hình

    @NotBlank(message = "Thông tin camera sau không được để trống")
    @Size(max = 255, message = "Thông tin camera sau không được vượt quá 255 kí tự")
    private String rearCamera;    // Camera sau

    @NotBlank(message = "Thông tin camera trước không được để trống")
    @Size(max = 255, message = "Thông tin camera trước không được vượt quá 255 kí tự")
    private String frontCamera;   // Camera trước

    @NotBlank(message = "Thông tin chip không được để trống")
    @Size(max = 255, message = "Thông tin chip không được vượt quá 255 kí tự")
    private String chipset;       // Chipset

    @NotNull(message = "Thông tin ram không được để trống")
    @Min(value = 1, message = "RAM phải lớn hơn 0")
    @Max(value = 999, message = "RAM không vượt quá 999 GB")
    private Integer ram;              // RAM

    @NotBlank(message = "Thông tin thẻ sim không được để trống")
    @Size(max = 255, message = "Thông tin thẻ sim không được vượt quá 255 kí tự")
    private String sim;           // Thẻ SIM

    @NotBlank(message = "Thông tin hệ điều hành không được để trống")
    @Size(max = 255, message = "Thông tin hệ điều hành không được vượt quá 255 kí tự")
    @Pattern(
            regexp = "^[a-zA-Z0-9\\s/-]+$",
            message = "Thông tin hệ điều hành chỉ được chứa chữ cái không dấu, số, dấu '/' và '-'"
    )
    private String operatingSystem;        // Hệ điều hành

    @NotBlank(message = "Thông tin cpu không được để trống")
    @Size(max = 255, message = "Thông tin cpu không được vượt quá 255 kí tự")
    private String cpu;           // CPU

    @NotBlank(message = "Thông tin sạc không được để trống")
    @Size(max = 255, message = "Thông tin sạc không được vượt quá 255 kí tự")
    private String charging;      // Công nghệ sạc

    // Hàm chuyển đổi String sang Float
    public void normalizeScreenSize() {
        if (screenSizeInput != null && screenSizeInput.matches("^\\d+(\\.\\d+)?$")) {
            this.screenSize = Float.parseFloat(screenSizeInput);
        } else {
            this.screenSize = null;
        }
    }
}
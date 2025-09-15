package com.example.SellPhone.DTO.Request.User;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {

    @NotNull(message = "ID người dùng không được để trống")
    @Min(value = 1, message = "ID người dùng không hợp lệ")
    private Long userId;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullname;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^[0-9]{12}$", message = "CCCD chỉ được nhập số và giới hạn 12 số")
    private String CCCD;

    @NotBlank(message = "Ngày sinh không được để trống")
    @Pattern(regexp = "^[0-9/-]+$", message = "Ngày sinh chỉ có thể nhập 0-9 và kí tự '/' hoặc '-'")
    private String dob;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Pattern(regexp = "^[\\p{L}\\s,\\.\\-0-9]+$", message = "Địa chỉ không hợp lệ")
    private String address;

}

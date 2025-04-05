package com.example.SellPhone.DTO.Request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserCreationRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullname;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ, phải bắt đầu bằng +84 hoặc 0 và có 10 chữ số")
    private String phone;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^[0-9]{12}$", message = "CCCD chỉ được nhập số và giới hạn 12 số")
    private String CCCD;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Pattern(regexp = "^[\\p{L}\\s,\\.\\-0-9]+$", message = "Địa chỉ không hợp lệ")
    private String address;

//    @NotBlank(message = "Vai trò không được để trống")
//    private String role;

    @NotBlank(message = "Ngày sinh không được để trống")
    @Pattern(regexp = "^[0-9/-]+$", message = "Ngày sinh chỉ có thể nhập 0-9 và kí tự '/' hoặc '-'")
    private String dob;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

//    @NotBlank(message = "Trạng thái không được để trống")
//    private String status;
}

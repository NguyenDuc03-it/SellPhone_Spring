package com.example.SellPhone.DTO.Request.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String fullname;
    private String phone;
    private String CCCD;
    private String password;
    private String address;
    private String role;
    private String dob;
    private String gender;
    private String status;
}

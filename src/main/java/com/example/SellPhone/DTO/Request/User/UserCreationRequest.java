package com.example.SellPhone.DTO.Request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserCreationRequest {

    private String fullname;
    private String email;
    private String phone;
    private String CCCD;
    private String password;
    private String address;
    private String role;
    private String dob;
    private String gender;
    private String status;
}

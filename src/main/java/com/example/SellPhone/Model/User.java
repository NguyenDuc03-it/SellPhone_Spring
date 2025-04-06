package com.example.SellPhone.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "CCCD")
    private String CCCD;

    @Column(name = "address")
    private String address;

    @Column(name = "role")
    private String role;

    @Column(name = "dob")
    private String dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "status")
    private String status;

    public String getRoleWithPrefix(){
        String position = "";
        if(role.equals("Admin")){
            position = "ROLE_ADMIN";
        }
        else if(role.equals("Khách hàng")){
            position = "ROLE_CUSTOMER";
        }
        else if(role.equals("Nhân viên")){
            position = "ROLE_STAFF";
        }
        return position;
    }
}

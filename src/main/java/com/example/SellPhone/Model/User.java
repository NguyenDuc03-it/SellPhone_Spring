package com.example.SellPhone.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "CCCD")
    private String CCCD;

    @Column(name = "address")
    private String address;

    @Column(name = "role")
    private String role;

    @Column(name = "dob")
    private String dob;

    @Column(name = "sex")
    private String sex;

    @Column(name = "status")
    private String status;
}

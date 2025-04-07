package com.example.SellPhone.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "specifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specification_id")
    private Long specificationId;

    @Column(name = "screen_size")
    private String screenSize;

    @Column(name = "rear_camera")
    private String rearCamera;

    @Column(name = "front_camera")
    private String frontCamera;

    @Column(name = "chipset")
    private String chipset;

    @Column(name = "ram")
    private String ram;

    @Column(name = "rom")
    private String rom;

    @Column(name = "sim")
    private String sim;

    @Column(name = "system")
    private String system;

    @Column(name = "cpu")
    private String cpu;

    @ManyToMany(mappedBy = "specifications")
    private List<Product> products;
}

package com.example.SellPhone.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "specifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specification_id")
    private Long specificationId;

    @Column(name = "screen_size")
    private float screenSize;

    @Column(name = "rear_camera")
    private String rearCamera;

    @Column(name = "front_camera")
    private String frontCamera;

    @Column(name = "chipset")
    private String chipset;

    @Column(name = "ram")
    private int ram;

//    @Column(name = "rom")
//    private int rom;

    @Column(name = "sim")
    private String sim;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "cpu")
    private String cpu;

    @Column(name = "charging")
    private String charging;

//    @ManyToMany(mappedBy = "specifications")
//    @JsonIgnore
//    private List<Product> products;

    @OneToMany(mappedBy = "specification", cascade = CascadeType.ALL)
    private List<SpecificationVariant> variants;
}

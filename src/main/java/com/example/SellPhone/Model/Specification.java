package com.example.SellPhone.Model;

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

    @Column(name = "sim")
    private String sim;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "cpu")
    private String cpu;

    @Column(name = "charging")
    private String charging;

    @OneToMany(mappedBy = "specification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SpecificationVariant> variants;
}

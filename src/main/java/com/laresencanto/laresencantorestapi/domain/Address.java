package com.laresencanto.laresencantorestapi.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "address")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "cep")
    private String cep;

    @Column(name = "residencetype")
    private String residenceType;

    @Column(name = "addresstype")
    private String addressType;

    @Column(name = "streetname")
    private String streetName;

    @Column(name = "addressnumber")
    private String addressNumber;

    @Column(name = "neighborhoods")
    private String neighborhoods;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "observations")
    private String observations;

}

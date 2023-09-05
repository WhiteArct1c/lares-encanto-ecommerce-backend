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

    public Address(String title, String cep, String residenceType, String addressType, String streetName, String addressNumber, String neighborhoods, String state, String city, String country, String observations) {
        this.title = title;
        this.cep = cep;
        this.residenceType = residenceType;
        this.addressType = addressType;
        this.streetName = streetName;
        this.addressNumber = addressNumber;
        this.neighborhoods = neighborhoods;
        this.state = state;
        this.city = city;
        this.country = country;
        this.observations = observations;
    }
}

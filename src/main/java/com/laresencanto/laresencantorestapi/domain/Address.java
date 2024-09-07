package com.laresencanto.laresencantorestapi.domain;

import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;


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

    @ElementCollection(targetClass = AddressCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "address_category", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private List<AddressCategory> categories;

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

    public Address(
            String title,
            String cep,
            String residenceType,
            String addressType,
            List<AddressCategory> categories,
            String streetName,
            String addressNumber,
            String neighborhoods,
            String state,
            String city,
            String country,
            String observations
    ) {
        this.title = title;
        this.cep = cep;
        this.residenceType = residenceType;
        this.addressType = addressType;
        this.categories = categories;
        this.streetName = streetName;
        this.addressNumber = addressNumber;
        this.neighborhoods = neighborhoods;
        this.state = state;
        this.city = city;
        this.country = country;
        this.observations = observations;
    }
}

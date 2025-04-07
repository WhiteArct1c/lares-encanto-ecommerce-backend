package com.laresencanto.laresencantorestapi.domain.address;

import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Table(name = "address")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Data
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

    @ElementCollection(targetClass = AddressCategory.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "address_category", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<AddressCategory> categories;

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

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "customer_address",
            joinColumns = @JoinColumn(name = "address_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Customer customer;

    public Address(
            String title,
            String cep,
            String residenceType,
            String addressType,
            Set<AddressCategory> categories,
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

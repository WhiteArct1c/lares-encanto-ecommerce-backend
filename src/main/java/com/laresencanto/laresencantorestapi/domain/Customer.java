package com.laresencanto.laresencantorestapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;


@Entity
@Table(name = "customer")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "birthdate")
    private String birthDate;

    @Column(name = "cellphone")
    private String phone;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "genderid")
    private Gender gender;

    @Column(name = "ranking")
    private String ranking;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userid")
    private User user;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Address> address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<CreditCard> creditCardList;
}

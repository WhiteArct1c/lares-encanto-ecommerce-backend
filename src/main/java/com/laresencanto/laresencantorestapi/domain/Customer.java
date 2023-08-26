package com.laresencanto.laresencantorestapi.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "genderid")
    private Gender gender;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userid")
    private User user;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Address> address;
}

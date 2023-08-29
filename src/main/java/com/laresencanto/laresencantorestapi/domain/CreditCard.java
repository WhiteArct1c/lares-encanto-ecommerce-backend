package com.laresencanto.laresencantorestapi.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "credit_card")
@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_owner")
    private String cardOwner;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "month_expiration")
    private String monthExpiration;

    @Column(name = "year_expiration")
    private String yearExpiration;

    @Column(name = "card_code")
    private String cardCode;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}

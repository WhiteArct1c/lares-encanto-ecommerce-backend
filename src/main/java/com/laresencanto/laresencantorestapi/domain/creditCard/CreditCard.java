package com.laresencanto.laresencantorestapi.domain.creditCard;

import com.laresencanto.laresencantorestapi.domain.customer.Customer;
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

    @Column(name = "card_flag")
    private String cardFlag;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_code")
    private String cardCode;

    @Column(name = "main_card")
    private boolean mainCard;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}

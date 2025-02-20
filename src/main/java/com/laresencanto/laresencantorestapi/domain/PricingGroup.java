package com.laresencanto.laresencantorestapi.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pricing_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profit_margin", nullable = false)
    private Double profitMargin;

}

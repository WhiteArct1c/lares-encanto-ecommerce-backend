package com.laresencanto.laresencantorestapi.domain.shipping;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_options")
@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ShippingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "delivery_time", nullable = false)
    private String deliveryTime;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @Column(name = "price_per_kg", nullable = false)
    private Double pricePerKg;

    @Column(name = "min_weight_kg", nullable = false)
    private Double minWeightKg;

    @Column(name = "max_weight_kg", nullable = false)
    private Double maxWeightKg;

    @Column(nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}


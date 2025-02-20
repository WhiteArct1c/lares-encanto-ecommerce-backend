package com.laresencanto.laresencantorestapi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "previous_status", nullable = false)
    private Boolean previousStatus; // Estado antes da alteração (true = ativo, false = inativo)

    @Column(name = "new_status", nullable = false)
    private Boolean newStatus; // Novo estado após a alteração (true = ativo, false = inativo)

    @Column(columnDefinition = "TEXT")
    private String reason; // Motivo da alteração

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}

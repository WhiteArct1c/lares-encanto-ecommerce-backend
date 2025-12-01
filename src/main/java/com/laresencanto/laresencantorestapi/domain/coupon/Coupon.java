package com.laresencanto.laresencantorestapi.domain.coupon;

import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.exchange.Exchange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // Código do cupom (ex: TROCA-2024-001)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value; // Valor do crédito

    @Column(name = "used_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal usedValue; // Valor já utilizado

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Data de expiração (opcional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer; // Null para cupons promocionais, obrigatório para cupons de troca

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id")
    private Exchange exchange; // Troca que gerou este cupom

    @Column(name = "coupon_type", nullable = false, length = 20)
    private String couponType; // "EXCHANGE" ou "PROMOTIONAL"

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calcula o valor disponível do cupom
     */
    public BigDecimal getAvailableValue() {
        return value.subtract(usedValue);
    }

    /**
     * Verifica se o cupom está válido (ativo e não expirado)
     */
    public Boolean isValid() {
        if (!isActive) {
            return false;
        }
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        return getAvailableValue().compareTo(BigDecimal.ZERO) > 0;
    }
}


package com.laresencanto.laresencantorestapi.dto.request.coupon;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionalCouponCreateDTO(
        @NotBlank(message = "Código do cupom é obrigatório")
        @Size(max = 50, message = "Código do cupom deve ter no máximo 50 caracteres")
        String code,

        @NotNull(message = "Valor do cupom é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor do cupom deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "Valor do cupom deve ter no máximo 8 dígitos inteiros e 2 decimais")
        BigDecimal value,

        // Quantidade máxima de usos (opcional). Se null, o cupom nunca esgota por quantidade de usos.
        @Positive(message = "Quantidade máxima de usos deve ser positiva")
        Integer maxUses,

        @Positive(message = "ID do cliente deve ser positivo")
        Long customerId, // Opcional - se null, cupom é válido para qualquer cliente

        LocalDateTime expiresAt // Opcional - se null, não expira
) {
        /**
         * Valida que a data de expiração seja no futuro (se fornecida)
         */
        @AssertTrue(message = "Data de expiração deve ser no futuro")
        public boolean isValidExpirationDate() {
            return expiresAt == null || expiresAt.isAfter(LocalDateTime.now());
        }
}


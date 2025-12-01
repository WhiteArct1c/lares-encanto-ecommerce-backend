package com.laresencanto.laresencantorestapi.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CouponUsageDTO(
        @NotBlank(message = "Código do cupom é obrigatório")
        String couponCode,
        
        @NotNull(message = "Valor a ser usado do cupom é obrigatório")
        @Positive(message = "Valor a ser usado deve ser maior que zero")
        BigDecimal amountToUse
) {
}


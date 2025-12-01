package com.laresencanto.laresencantorestapi.dto.request.exchange;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para representar um item individual de troca/devolução
 */
public record ExchangeItemDTO(
        @NotNull(message = "ID do produto do pedido é obrigatório")
        Long orderProductId,
        
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity,
        
        String reason // Motivo específico para este item (opcional)
) {
}


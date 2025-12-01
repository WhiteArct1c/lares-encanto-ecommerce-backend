package com.laresencanto.laresencantorestapi.dto.request.exchange;

import jakarta.validation.constraints.NotNull;

public record ExchangeAuthorizationDTO(
        @NotNull(message = "ID da troca é obrigatório")
        Long exchangeId,
        
        @NotNull(message = "Ação é obrigatória")
        String action // "APPROVE" ou "REJECT"
) {
}


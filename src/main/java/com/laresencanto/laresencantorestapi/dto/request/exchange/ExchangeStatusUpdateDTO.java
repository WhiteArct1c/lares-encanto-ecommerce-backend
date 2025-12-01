package com.laresencanto.laresencantorestapi.dto.request.exchange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExchangeStatusUpdateDTO(
        @NotNull(message = "ID da troca é obrigatório")
        Long exchangeId,
        
        @NotBlank(message = "Nome do status é obrigatório")
        String statusName
) {
}


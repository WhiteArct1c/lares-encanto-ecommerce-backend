package com.laresencanto.laresencantorestapi.dto.request.exchange;

import jakarta.validation.constraints.NotNull;

public record ExchangeConfirmationDTO(
        @NotNull(message = "ID da troca é obrigatório")
        Long exchangeId,
        
        @NotNull(message = "Informação sobre retorno ao estoque é obrigatória")
        Boolean returnToStock // Se os itens devem retornar ao estoque
) {
}


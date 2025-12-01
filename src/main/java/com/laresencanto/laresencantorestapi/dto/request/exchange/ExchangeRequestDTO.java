package com.laresencanto.laresencantorestapi.dto.request.exchange;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO para solicitar troca/devolução de um ou mais produtos
 * Permite trocar múltiplos produtos com quantidades diferentes em uma única requisição
 */
public record ExchangeRequestDTO(
        @NotNull(message = "ID do pedido é obrigatório")
        Long orderId,
        
        @NotEmpty(message = "Pelo menos um item deve ser informado para troca/devolução")
        @Valid
        List<ExchangeItemDTO> items // Lista de produtos a serem trocados/devolvidos
) {
}


package com.laresencanto.laresencantorestapi.dto.request.customer;

import jakarta.validation.constraints.NotNull;

public record CreditCardRequestDTO(
        @NotNull(message = "O token não pode ser nulo")
        String token,

        Long id,

        @NotNull(message = "O número do cartão é obrigatório")
        Long cardNumber,

        @NotNull(message = "O nome do cartão é obrigatório")
        String cardName,

        @NotNull(message = "O número do cartão é obrigatório")
        Long cardCode,

        boolean mainCard,

        @NotNull(message = "A bandeira do cartão é obrigatória")
        String cardFlag
) {
}

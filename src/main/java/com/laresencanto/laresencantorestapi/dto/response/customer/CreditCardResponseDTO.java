package com.laresencanto.laresencantorestapi.dto.response.customer;

public record CreditCardResponseDTO(
        Long id,
        String cardNumber,
        String cardName,
        String cardCode,
        String cardFlag,
        boolean mainCard
) {
}

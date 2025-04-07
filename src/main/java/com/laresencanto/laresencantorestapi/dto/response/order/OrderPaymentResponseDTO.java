package com.laresencanto.laresencantorestapi.dto.response.order;

import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;

import java.math.BigDecimal;

public record OrderPaymentResponseDTO(
        Long id,
        Integer installments,
        BigDecimal installmentValue,
        String paymentMethod,
        CreditCardResponseDTO creditCard
) {
}

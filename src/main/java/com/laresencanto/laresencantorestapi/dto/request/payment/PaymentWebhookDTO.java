package com.laresencanto.laresencantorestapi.dto.request.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentWebhookDTO(
        @NotNull(message = "ID do pedido é obrigatório")
        Long orderId,
        @NotNull(message = "Status do pagamento é obrigatório")
        String paymentStatus, // "APPROVED", "REJECTED", "PENDING"
        String transactionId,
        String message
) {
}


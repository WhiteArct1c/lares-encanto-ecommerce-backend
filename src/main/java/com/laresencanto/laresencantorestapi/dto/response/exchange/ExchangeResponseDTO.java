package com.laresencanto.laresencantorestapi.dto.response.exchange;

import com.laresencanto.laresencantorestapi.dto.response.order.OrderStatusResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record ExchangeResponseDTO(
    Long id,
    Long orderId,
    List<ExchangeItemResponseDTO> items, // Lista de itens da troca/devolução
    OrderStatusResponseDTO status,
    Boolean returnToStock,
    Boolean couponGenerated,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
}

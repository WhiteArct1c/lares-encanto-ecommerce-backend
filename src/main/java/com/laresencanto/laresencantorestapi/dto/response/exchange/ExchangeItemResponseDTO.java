package com.laresencanto.laresencantorestapi.dto.response.exchange;

import com.laresencanto.laresencantorestapi.dto.response.order.OrderProductResponseDTO;

public record ExchangeItemResponseDTO(
        Long id,
        OrderProductResponseDTO orderProduct,
        Integer quantity,
        String reason
) {
}


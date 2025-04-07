package com.laresencanto.laresencantorestapi.dto.response.order;

public record OrderShipmentResponseDTO(
        Long id,
        String name,
        String deliveryTime,
        Double price
) {
}

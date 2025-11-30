package com.laresencanto.laresencantorestapi.dto.response.shipping;

public record ShippingOptionResponseDTO(
        Long id,
        String name,
        String deliveryTime,
        Double price
) {
}


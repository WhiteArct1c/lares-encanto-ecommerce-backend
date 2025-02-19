package com.laresencanto.laresencantorestapi.dto.response.product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        String color,
        String image,
        Boolean isActive,
        String categoryName,
        String type,
        Integer stockQuantity
) {
}

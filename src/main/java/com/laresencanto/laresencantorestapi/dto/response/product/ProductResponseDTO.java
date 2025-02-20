package com.laresencanto.laresencantorestapi.dto.response.product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        String color,
        String image,
        Boolean isActive,
        String categoryName,
        String pricingGroup,
        String type,
        Integer stockQuantity
) {
}

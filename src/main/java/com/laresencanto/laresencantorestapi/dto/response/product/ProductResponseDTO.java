package com.laresencanto.laresencantorestapi.dto.response.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record ProductResponseDTO(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        String color,
        String image,
        Boolean isActive,
        ProductCategoryResponseDTO category,
        PricingGroupResponseDTO pricingGroup,
        String type,
        Integer stockQuantity,
        Double weightKg
) {
}

package com.laresencanto.laresencantorestapi.dto.response.product;

import com.laresencanto.laresencantorestapi.dto.response.pricingGroup.PricingGroupResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.productCategory.ProductCategoryResponseDTO;

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
        ProductCategoryResponseDTO category,
        PricingGroupResponseDTO pricingGroup,
        String type,
        Integer stockQuantity
) {
}

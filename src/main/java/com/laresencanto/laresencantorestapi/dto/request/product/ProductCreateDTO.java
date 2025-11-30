package com.laresencanto.laresencantorestapi.dto.request.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductCreateDTO(
                String name,
                String description,
                BigDecimal price,
                String color,
                MultipartFile image,
                Integer categoryId,
                Integer pricingGroupId,
                String type,
                Integer initialStockQuantity,
                Double weightKg) {
}

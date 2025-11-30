package com.laresencanto.laresencantorestapi.dto.request.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductUpdateDTO(
                Integer id,
                String name,
                String description,
                BigDecimal price,
                String color,
                MultipartFile image,
                Boolean isActive,
                Integer categoryId,
                Integer pricingGroupId,
                Integer stockQuantity,
                String type,
                Double weightKg) {
}

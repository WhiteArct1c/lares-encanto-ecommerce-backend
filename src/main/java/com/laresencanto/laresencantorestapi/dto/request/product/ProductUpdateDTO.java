package com.laresencanto.laresencantorestapi.dto.request.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductUpdateDTO(
        String name,
        String description,
        BigDecimal price,
        String color,
        MultipartFile image,
        Boolean isActive,
        Integer categoryId,
        String type
) {
}

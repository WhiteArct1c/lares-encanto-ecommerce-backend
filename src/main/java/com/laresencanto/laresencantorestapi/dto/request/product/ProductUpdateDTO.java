package com.laresencanto.laresencantorestapi.dto.request.product;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import java.util.List;

public record ProductUpdateDTO(
                Integer id,
                String name,
                String description,
                BigDecimal price,
                String color, // Mantido para compatibilidade
                MultipartFile image,
                Boolean isActive,
                Integer categoryId,
                Integer pricingGroupId,
                Integer stockQuantity,
                String type,
                Double weightKg,
                List<String> colorHexCodes, // Lista de códigos hexadecimais de cores
                List<String> tagNames) { // Lista de nomes de tags
}

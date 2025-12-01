package com.laresencanto.laresencantorestapi.dto.response.product;

import java.util.List;

public record ImageSearchResponseDTO(
        List<String> detectedLabels,
        List<String> detectedColors,
        List<String> detectedObjects,
        List<ProductMatchDTO> matchedProducts,
        String searchMethod
) {
    public record ProductMatchDTO(
            Integer productId,
            String productName,
            Double similarityScore,
            List<String> matchReasons,
            String productImage
    ) {}
}


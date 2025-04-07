package com.laresencanto.laresencantorestapi.dto.response.order;

import com.laresencanto.laresencantorestapi.dto.response.product.ProductResponseDTO;

public record OrderProductResponseDTO(
        Long id,
        Integer quantity,
        ProductResponseDTO product
) {
}

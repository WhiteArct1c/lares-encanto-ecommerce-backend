package com.laresencanto.laresencantorestapi.dto.response.dashboard;

import java.util.List;

public record SalesByCategoryResponseDTO(
        Integer year,
        Integer month,
        String monthLabel,
        List<CategorySalesDTO> categories
) {
    public record CategorySalesDTO(
            Integer categoryId,
            String categoryName,
            Double totalSalesAmount,
            Long totalOrders
    ) {
    }
}



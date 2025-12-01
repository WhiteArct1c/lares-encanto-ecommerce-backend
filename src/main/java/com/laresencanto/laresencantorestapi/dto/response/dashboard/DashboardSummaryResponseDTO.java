package com.laresencanto.laresencantorestapi.dto.response.dashboard;

public record DashboardSummaryResponseDTO(
        Double currentMonthSalesTotal,
        Long currentMonthRegisteredUsers,
        Long todaySalesCount
) {
}



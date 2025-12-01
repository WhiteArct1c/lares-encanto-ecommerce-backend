package com.laresencanto.laresencantorestapi.dto.response.order;

import java.math.BigDecimal;

public record OrderCouponResponseDTO(
        Long id,
        String couponCode,
        String couponType,
        BigDecimal amountUsed
) {
}


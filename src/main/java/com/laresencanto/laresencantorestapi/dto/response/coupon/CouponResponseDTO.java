package com.laresencanto.laresencantorestapi.dto.response.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponseDTO(
        Long id,
        String code,
        BigDecimal value,
        BigDecimal usedValue,
        BigDecimal availableValue,
        Boolean isActive,
        LocalDateTime expiresAt,
        Long customerId,
        String couponType,
        Integer maxUses,
        Integer usedCount
) {
}


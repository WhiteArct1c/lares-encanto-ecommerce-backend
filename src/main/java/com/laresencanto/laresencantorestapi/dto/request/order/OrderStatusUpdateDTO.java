package com.laresencanto.laresencantorestapi.dto.request.order;

public record OrderStatusUpdateDTO(
    Long orderId,
    String statusName) {
}

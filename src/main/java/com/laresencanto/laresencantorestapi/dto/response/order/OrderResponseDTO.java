package com.laresencanto.laresencantorestapi.dto.response.order;

import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;

import java.time.LocalDateTime;
import java.util.Set;

public record OrderResponseDTO(
        Long id,
        CustomerResponseDTO customer,
        AddressRequestDTO address,
        OrderStatusResponseDTO status,
        String type,
        Set<OrderProductResponseDTO> orderProducts,
        Set<OrderPaymentResponseDTO> orderPayments,
        OrderShipmentResponseDTO shipment,
        Double totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

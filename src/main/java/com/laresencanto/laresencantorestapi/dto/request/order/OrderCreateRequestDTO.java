package com.laresencanto.laresencantorestapi.dto.request.order;

import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderPaymentResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderProductResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderShipmentResponseDTO;

import java.util.List;
import java.util.Set;

public record OrderCreateRequestDTO(
                AddressRequestDTO address,
                Set<OrderPaymentResponseDTO> orderPayments,
                Set<OrderProductResponseDTO> orderProducts,
                OrderShipmentResponseDTO shipping,
                String type,
                Double totalPrice,
                List<CouponUsageDTO> coupons
) {
}

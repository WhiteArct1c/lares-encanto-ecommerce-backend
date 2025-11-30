package com.laresencanto.laresencantorestapi.dto.request.shipping;

import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.order.OrderProductResponseDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record ShippingCalculationRequestDTO(
        @NotNull(message = "O endereço de entrega é obrigatório")
        AddressRequestDTO address,

        @NotEmpty(message = "É necessário informar pelo menos um produto")
        Set<OrderProductResponseDTO> products
) {
}


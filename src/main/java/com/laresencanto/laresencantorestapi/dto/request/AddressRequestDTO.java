package com.laresencanto.laresencantorestapi.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record AddressRequestDTO(
        String title,
        String cep,
        String residenceType,
        String addressType,
        String streetName,
        String addressNumber,
        String neighborhoods,
        String city,

        @NotEmpty(message = "teste")
        String state,
        String country,
        String observations
) {
}

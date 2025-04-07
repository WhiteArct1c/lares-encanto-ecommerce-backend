package com.laresencanto.laresencantorestapi.dto.response.address;

import java.util.Set;

public record AddressResponseDTO(
        Long id,
        String title,
        String cep,
        String residenceType,
        String addressType,
        Set<String> categories,
        String streetName,
        String addressNumber,
        String neighborhoods,
        String state,
        String city,
        String country,
        String observations
) {
}

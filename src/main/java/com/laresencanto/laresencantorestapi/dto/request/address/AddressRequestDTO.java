package com.laresencanto.laresencantorestapi.dto.request.address;

public record AddressRequestDTO(
        String title,
        String cep,
        String residenceType,
        String addressType,
        String streetName,
        String addressNumber,
        String neighborhoods,
        String city,
        String state,
        String country,
        String observations
) {
}

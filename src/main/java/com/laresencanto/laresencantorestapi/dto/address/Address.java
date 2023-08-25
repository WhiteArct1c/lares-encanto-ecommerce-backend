package com.laresencanto.laresencantorestapi.dto.address;

public record Address(
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

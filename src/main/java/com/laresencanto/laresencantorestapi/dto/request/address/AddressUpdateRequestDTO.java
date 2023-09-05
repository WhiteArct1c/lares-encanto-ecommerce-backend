package com.laresencanto.laresencantorestapi.dto.request.address;

public record AddressUpdateRequestDTO(
        String token,
        AddressRequestDTO address
) {
}

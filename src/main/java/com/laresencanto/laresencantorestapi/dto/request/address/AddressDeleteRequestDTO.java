package com.laresencanto.laresencantorestapi.dto.request.address;

import com.laresencanto.laresencantorestapi.domain.Address;

public record AddressDeleteRequestDTO(
        String token,
        Address address
) {
}

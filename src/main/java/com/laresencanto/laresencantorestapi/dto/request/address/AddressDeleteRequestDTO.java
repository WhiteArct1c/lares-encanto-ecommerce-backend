package com.laresencanto.laresencantorestapi.dto.request.address;

import com.laresencanto.laresencantorestapi.domain.address.Address;

public record AddressDeleteRequestDTO(
        String token,
        Address address
) {
}

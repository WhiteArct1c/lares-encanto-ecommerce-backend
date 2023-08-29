package com.laresencanto.laresencantorestapi.dto.response.customer;

import com.laresencanto.laresencantorestapi.domain.Address;
import com.laresencanto.laresencantorestapi.domain.Gender;

import java.util.Set;

public record CustomerResponseDTO(
        Long id,
        String fullName,
        String cpf,
        String birthDate,
        String phone,
        Gender gender,
        Set<Address> addresses
) {
}

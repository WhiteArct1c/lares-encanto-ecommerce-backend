package com.laresencanto.laresencantorestapi.dto.response.customer;

import com.laresencanto.laresencantorestapi.domain.Address;
import com.laresencanto.laresencantorestapi.domain.Gender;

import java.util.List;
import java.util.Set;

public record CustomerResponseAdminDTO(
        Long id,
        String fullName,
        String cpf,
        String birthDate,
        String phone,
        Gender gender,
        String ranking,
        String userRole,
        String isActive,
        Set<Address> addresses,
        List<CreditCardResponseDTO> creditCards
) {
}

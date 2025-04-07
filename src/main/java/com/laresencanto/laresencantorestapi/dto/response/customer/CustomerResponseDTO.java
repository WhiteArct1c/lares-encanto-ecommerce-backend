package com.laresencanto.laresencantorestapi.dto.response.customer;

import com.laresencanto.laresencantorestapi.domain.customer.Gender;
import com.laresencanto.laresencantorestapi.dto.response.address.AddressResponseDTO;

import java.util.List;
import java.util.Set;

public record CustomerResponseDTO(
        Long id,
        String fullName,
        String cpf,
        String birthDate,
        String phone,
        Gender gender,
        Set<AddressResponseDTO> addresses,
        List<CreditCardResponseDTO> creditCards
) {
}

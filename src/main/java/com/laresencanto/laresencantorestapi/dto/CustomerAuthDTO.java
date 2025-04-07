package com.laresencanto.laresencantorestapi.dto;

import com.laresencanto.laresencantorestapi.domain.customer.Gender;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;

import java.util.List;

public record CustomerAuthDTO(
        Long id,
        String fullName,
        String cpf,
        String birthDate,
        String phone,
        Gender gender,
        String ranking,
        List<AddressRequestDTO> addresses,
        List<CreditCardResponseDTO> creditCards
) {
}

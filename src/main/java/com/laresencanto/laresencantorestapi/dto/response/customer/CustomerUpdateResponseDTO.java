package com.laresencanto.laresencantorestapi.dto.response.customer;

import com.laresencanto.laresencantorestapi.domain.Gender;

public record CustomerUpdateResponseDTO(
        Long id,
        String fullName,
        String cpf,
        String birthDate,
        String phone,
        Gender gender
) {
}

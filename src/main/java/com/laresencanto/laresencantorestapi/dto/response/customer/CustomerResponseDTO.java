package com.laresencanto.laresencantorestapi.dto.response.customer;

public record CustomerResponseDTO(
        String fullName,
        String cpf,
        String birthDate,
        String phone
) {
}

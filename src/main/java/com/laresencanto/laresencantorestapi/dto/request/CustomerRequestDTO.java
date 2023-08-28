package com.laresencanto.laresencantorestapi.dto.request;

import com.laresencanto.laresencantorestapi.utils.enums.Gender;
import org.hibernate.validator.constraints.br.CPF;

public record CustomerRequestDTO(
        RegisterRequestDTO user,
        String fullName,

        @CPF(message = "CPF deve ser válido")
        String cpf,
        String birthDate,
        String phone,
        Gender gender,
        AddressRequestDTO address
) {
}

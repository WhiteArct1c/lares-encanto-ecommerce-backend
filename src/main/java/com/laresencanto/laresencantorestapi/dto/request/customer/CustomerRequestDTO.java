package com.laresencanto.laresencantorestapi.dto.request.customer;

import com.laresencanto.laresencantorestapi.dto.request.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.utils.enums.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

public record CustomerRequestDTO(

        @NotNull(message = "Usuário não deve ser nulo")
        RegisterRequestDTO user,
        @NotEmpty(message = "Nome completo não pode ser nulo ou vazio")
        String fullName,
        @CPF(message = "CPF deve ser válido")
        String cpf,
        @NotEmpty(message = "Data de nascimento não pode ser nulo ou vazio")
        String birthDate,
        @NotEmpty(message = "Telefone não pode ser nulo ou vazio")
        String phone,
        @NotNull(message = "Gênero não pode ser nulo ou vazio")
        Gender gender,
        @NotNull(message = "Endereço não pode ser nulo")
        AddressRequestDTO address
) {
}

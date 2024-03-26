package com.laresencanto.laresencantorestapi.dto.request.address;

import jakarta.validation.constraints.NotEmpty;

public record AddressRequestDTO(
        String id,

        @NotEmpty(message = "O título do endereço é obrigatório")
        String title,

        @NotEmpty(message = "O CEP é obrigatório")
        String cep,

        @NotEmpty(message = "O típo da residência é obrigatório")
        String residenceType,

        @NotEmpty(message = "O tipo de endereço é obrigatório")
        String addressType,

        @NotEmpty(message = "O nome do logradouro é obrigatório")
        String streetName,

        @NotEmpty(message = "O número do logradouro é obrigatório")
        String addressNumber,

        @NotEmpty(message = "O bairro é obrigatória")
        String neighborhoods,

        @NotEmpty(message = "A cidade é obrigatória")
        String city,

        @NotEmpty(message = "O estado é obrigatório")
        String state,

        @NotEmpty(message = "O país é obrigatório")
        String country,

        @NotEmpty(message = "As observações são obrigatórias")
        String observations
) {
}

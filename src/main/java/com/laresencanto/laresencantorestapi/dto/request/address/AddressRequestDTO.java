package com.laresencanto.laresencantorestapi.dto.request.address;

import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

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

        @NotEmpty(message = "A categoria do endereço é obrigatória")
        List<String> addressCategories,

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

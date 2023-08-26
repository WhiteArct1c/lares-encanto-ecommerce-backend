package com.laresencanto.laresencantorestapi.dto.request.customer;

import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.user.UserRequestDTO;
import com.laresencanto.laresencantorestapi.utils.enums.Gender;

public record CustomerRequestDTO(
        UserRequestDTO user,
        String fullName,
        String cpf,
        String birthDate,
        String phone,
        Gender gender,
        AddressRequestDTO address
) {
}

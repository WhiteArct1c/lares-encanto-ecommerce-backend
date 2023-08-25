package com.laresencanto.laresencantorestapi.dto.costumer;

import com.laresencanto.laresencantorestapi.dto.address.Address;
import com.laresencanto.laresencantorestapi.dto.user.User;
import com.laresencanto.laresencantorestapi.utils.enums.Gender;

public record CustomerDTO(
        User user,
        String fullName,
        String cpf,
        String birthDate,
        String cellphone,
        Gender gender,
        Address address
) {
}

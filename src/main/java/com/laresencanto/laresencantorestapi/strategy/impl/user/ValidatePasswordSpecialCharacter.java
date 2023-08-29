package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordSpecialCharacter implements IStrategy<CustomerRequestDTO> {
    @Override
    public String validate(CustomerRequestDTO data) {
        if (!data.user().password().matches(".*[!@#$%^&*()].*")) {
            return "A senha deve conter pelo menos um caractere especial.";
        }
        return "";
    }
}

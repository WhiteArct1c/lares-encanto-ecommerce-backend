package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordLowercase implements IStrategy<CustomerRequestDTO> {
    @Override
    public String validate(CustomerRequestDTO data) {
        if (!data.user().password().matches(".*[a-z].*")) {
            return "A senha deve conter pelo menos uma letra minúscula.";
        }
        return "";
    }
}

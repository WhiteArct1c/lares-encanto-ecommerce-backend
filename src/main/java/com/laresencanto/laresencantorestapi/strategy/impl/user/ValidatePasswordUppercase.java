package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordUppercase implements IStrategy<CustomerRequestDTO> {
    @Override
    public String validate(CustomerRequestDTO data) {
        if (!data.user().password().matches(".*[A-Z].*")) {
            return "A senha deve conter pelo menos uma letra maiúscula.";
        }
        return "";
    }
}

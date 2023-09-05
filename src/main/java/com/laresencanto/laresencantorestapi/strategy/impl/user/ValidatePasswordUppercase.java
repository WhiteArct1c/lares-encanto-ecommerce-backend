package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordUppercase implements IStrategy<RegisterRequestDTO> {
    @Override
    public String validate(RegisterRequestDTO data) {
        if (!data.password().matches(".*[A-Z].*")) {
            return "A senha deve conter pelo menos uma letra maiúscula.";
        }
        return "";
    }
}

package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordNotNull implements IStrategy<RegisterRequestDTO> {
    @Override
    public String validate(RegisterRequestDTO data) {
        if (data.password() == null) {
            return "A senha não pode ser nula.";
        }
        return "";
    }
}

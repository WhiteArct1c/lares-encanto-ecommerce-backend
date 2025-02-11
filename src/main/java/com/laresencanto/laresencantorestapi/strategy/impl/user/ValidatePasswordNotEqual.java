package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordNotEqual implements IStrategy<RegisterRequestDTO> {

    @Override
    public String validate(RegisterRequestDTO data) {
        if(!data.password().equals(data.confirmedPassword())){
            return "Senhas não são iguais.";
        }
        return "";
    }
}

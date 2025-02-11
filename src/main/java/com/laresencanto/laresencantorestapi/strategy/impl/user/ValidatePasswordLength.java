package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordLength implements IStrategy<RegisterRequestDTO> {

    @Override
    public String validate(RegisterRequestDTO data) {
        if(data.password().length() < 8){
            return "Senha deve ter ao menos 8 caracteres";
        }
        return "";
    }
}

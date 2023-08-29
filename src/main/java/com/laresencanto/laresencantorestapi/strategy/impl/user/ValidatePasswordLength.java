package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordLength implements IStrategy<CustomerRequestDTO> {

    @Override
    public String validate(CustomerRequestDTO data) {
        if(data.user().password().length() < 8){
            return "Senha deve ter ao menos 8 caracteres";
        }
        return "";
    }
}

package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordNotEqual implements IStrategy<CustomerRequestDTO> {

    @Override
    public String validate(CustomerRequestDTO data) {
        if(!data.user().password().equals(data.user().confirmedPassword())){
            return "Senhas não são iguais.";
        }
        return "";
    }
}

package com.laresencanto.laresencantorestapi.strategy.impl.user;

import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

public class ValidatePasswordNotNull implements IStrategy<CustomerRequestDTO> {
    @Override
    public String validate(CustomerRequestDTO data) {
        if (data.user().password() == null) {
            return "A senha não pode ser nula.";
        }
        return "";
    }
}

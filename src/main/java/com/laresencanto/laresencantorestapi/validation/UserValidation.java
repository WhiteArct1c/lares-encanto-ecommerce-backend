package com.laresencanto.laresencantorestapi.validation;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import com.laresencanto.laresencantorestapi.strategy.impl.user.*;

import java.util.ArrayList;
import java.util.List;

public class UserValidation {

    private final List<IStrategy<CustomerRequestDTO>> userRules = new ArrayList<>();

    public UserValidation(){
        //setting strategy classes for validation
        userRules.add(new ValidatePasswordNotNull());
        userRules.add(new ValidatePasswordNotEqual());
        userRules.add(new ValidatePasswordLength());
        userRules.add(new ValidatePasswordUppercase());
        userRules.add(new ValidatePasswordLowercase());
        userRules.add(new ValidatePasswordSpecialCharacter());
    }

    public String validateRules(CustomerRequestDTO customer){
        StringBuilder errors = new StringBuilder();
        for(IStrategy rule: userRules){
            String message = rule.validate(customer);

            if(!message.isEmpty()){
                errors.append(""+ message +" - ");
            }
        }

        return errors.toString();
    }
}

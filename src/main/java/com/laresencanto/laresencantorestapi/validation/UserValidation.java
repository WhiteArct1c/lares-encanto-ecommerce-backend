package com.laresencanto.laresencantorestapi.validation;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import com.laresencanto.laresencantorestapi.strategy.impl.user.ValidatePassword;

import java.util.ArrayList;
import java.util.List;

public class UserValidation {

    private final StringBuilder errors = new StringBuilder();
    private final List<IStrategy<CustomerRequestDTO>> userRules = new ArrayList<>();

    public UserValidation(){
        //setting strategy classes for validation
        userRules.add(new ValidatePassword());
    }

    public String validateRules(CustomerRequestDTO customer){
        for(IStrategy rule: userRules){
            String message = rule.validate(customer);

            if(!message.isEmpty()){
                errors.append(message + " ");
            }
        }

        return errors.toString();
    }
}

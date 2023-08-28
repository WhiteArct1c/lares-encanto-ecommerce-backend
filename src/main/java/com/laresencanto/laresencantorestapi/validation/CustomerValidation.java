package com.laresencanto.laresencantorestapi.validation;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import com.laresencanto.laresencantorestapi.strategy.impl.user.ValidatePassword;

import java.util.ArrayList;
import java.util.List;

public class CustomerValidation {

    private final StringBuilder errors = new StringBuilder();
    private final List<IStrategy<CustomerRequestDTO>> customerRules = new ArrayList<>();

    public CustomerValidation(){
        //setting strategy classes for validation
        //customerRules.add(new ValidatePassword());
    }

    public String validateRules(CustomerRequestDTO customer){
        for(IStrategy rule: customerRules){
            String message = rule.validate(customer);

            if(!message.isEmpty()){
                errors.append(message + " ");
            }
        }

        return errors.toString();
    }

}

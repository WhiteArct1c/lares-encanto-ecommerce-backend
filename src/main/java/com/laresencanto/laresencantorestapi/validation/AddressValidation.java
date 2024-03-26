package com.laresencanto.laresencantorestapi.validation;

import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;

import java.util.ArrayList;
import java.util.List;

public class AddressValidation {
    private final List<IStrategy<AddressRequestDTO>> addressRules = new ArrayList<>();

    public AddressValidation(){
        //setting strategy classes for validation

    }

    public String validateAddressRequestRules(AddressRequestDTO address){
        StringBuilder errors = new StringBuilder();
        for(IStrategy<AddressRequestDTO> rule: addressRules){
            String message = rule.validate(address);

            if(!message.isEmpty()){
                errors
                        .append(message)
                        .append(" - ");
            }
        }

        return errors.toString();
    }
}

package com.laresencanto.laresencantorestapi.validation;

import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import com.laresencanto.laresencantorestapi.strategy.impl.credit_card.ValidateFlag;
import com.laresencanto.laresencantorestapi.strategy.impl.credit_card.ValidateMainCard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreditCardValidation {
    private final List<IStrategy<CreditCardRequestDTO>> creditCardRules = new ArrayList<>();

    public CreditCardValidation( ValidateFlag validateFlag, ValidateMainCard validationMainCard ) {

        creditCardRules.add(validateFlag);
        creditCardRules.add(validationMainCard);
    }

    public String validateCreditCardRequestRules(CreditCardRequestDTO creditCardRequestDTO){
        StringBuilder errors = new StringBuilder();
        for(IStrategy<CreditCardRequestDTO> rule: creditCardRules){
            String message = rule.validate(creditCardRequestDTO);

            if(!message.isEmpty()){
                errors
                        .append(message)
                        .append(" - ");
            }
        }

        return errors.toString();
    }
}

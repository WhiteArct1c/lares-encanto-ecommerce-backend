package com.laresencanto.laresencantorestapi.strategy.impl.credit_card;

import com.laresencanto.laresencantorestapi.domain.CreditCardFlags;
import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.repository.CreditCardFlagsRepository;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ValidateFlag implements IStrategy<CreditCardRequestDTO> {

    private final CreditCardFlagsRepository creditCardFlagsRepository;

    @Autowired
    public ValidateFlag(CreditCardFlagsRepository creditCardFlagsRepository) {
        this.creditCardFlagsRepository = creditCardFlagsRepository;
    }

    @Override
    public String validate(CreditCardRequestDTO data) {
            List<CreditCardFlags> creditCardFlagsList = creditCardFlagsRepository.findAll().stream()
                    .filter(flag -> flag.getFlagName().equals(data.cardFlag()))
                    .toList();

            if(creditCardFlagsList.isEmpty()){
                return "Bandeira do cartão inválida!";
            }

        return "";
    }
}

package com.laresencanto.laresencantorestapi.strategy.impl.credit_card;

import com.laresencanto.laresencantorestapi.domain.CreditCard;
import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class ValidateUniqueMainCard implements IStrategy<CreditCardRequestDTO> {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TokenService tokenService;

    public ValidateUniqueMainCard(UserRepository userRepository, CustomerRepository customerRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.tokenService = tokenService;
    }

    @Override
    public String validate(CreditCardRequestDTO data) {
        var decodedToken = tokenService.decodedJwtToken(data.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);

        if(customer.isPresent()) {
            CreditCard customerMainCard = customer.get().getCreditCardList().stream().filter(CreditCard::isMainCard).findFirst().orElse(null);
            if(customerMainCard != null && Objects.equals(customerMainCard.getId(), data.id())){
                if(!data.mainCard()) {
                    return "Ao menos um cartão deve ser principal";
                }
            }
        }

        return "";
    }
}

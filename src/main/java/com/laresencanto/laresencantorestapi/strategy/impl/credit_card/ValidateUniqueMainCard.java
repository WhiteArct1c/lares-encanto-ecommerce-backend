package com.laresencanto.laresencantorestapi.strategy.impl.credit_card;

import com.laresencanto.laresencantorestapi.domain.creditCard.CreditCard;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.user.User;
import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.repository.CreditCardRepository;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class ValidateUniqueMainCard implements IStrategy<CreditCardRequestDTO> {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final TokenService tokenService;

    public ValidateUniqueMainCard(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            CreditCardRepository creditCardRepository,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.tokenService = tokenService;
    }

    @Override
    public String validate(CreditCardRequestDTO data) {
        var decodedToken = tokenService.decodedJwtToken(data.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);

        if(customer.isPresent()) {

            Set<CreditCard> customerCards = customer.get().getCreditCardList();
            CreditCard customerMainCard = customerCards.stream().filter(CreditCard::isMainCard).findFirst().orElse(null);

            if(customerMainCard != null && Objects.equals(customerMainCard.getId(), data.id())){
                if(!data.mainCard() && customerCards.size() == 1){
                    return "Ao menos um cartão deve ser principal";
                }
                try{
                    customerMainCard.setMainCard(false);
                    creditCardRepository.save(customerMainCard);
                    customerCards.stream()
                            .filter(card -> !card.isMainCard())
                            .findFirst()
                            .ifPresent(card -> {
                                card.setMainCard(true);
                                creditCardRepository.save(card);
                            });
                }catch (Exception e){
                    return "Erro ao trocar o cartão principal";
                }
            }
        }

        return "";
    }
}

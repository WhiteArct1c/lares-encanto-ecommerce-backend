package com.laresencanto.laresencantorestapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.laresencanto.laresencantorestapi.domain.creditCard.CreditCard;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CreditCardRepository;
import com.laresencanto.laresencantorestapi.validation.CreditCardValidation;

@Service
public class CreditCardService {

    private final CreditCardValidation creditCardValidation;
    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;


    public CreditCardService(
            CreditCardValidation creditCardValidation,
            CreditCardRepository creditCardRepository,
            CustomerRepository customerRepository
    ) {
        this.creditCardValidation = creditCardValidation;
        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
    }

    public ResponseDTO<CreditCardResponseDTO> getCreditCardById(Long id){
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();
        Optional<CreditCard> creditCard = creditCardRepository.findByIdAndCustomerId(id, customer.getId());

        if(creditCard.isEmpty()){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Cartão de crédito não encontrado",
                    null
            );
        }else{
            CreditCard card = creditCard.get();
            return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cartão de crédito encontrado com sucesso!",
                List.of(
                    new CreditCardResponseDTO(
                        card.getId(),
                        card.getCardNumber(),
                        card.getCardName(),
                        card.getCardCode(),
                        card.getCardFlag(),
                        card.isMainCard()
                    )
                )
            );
        }
    }

    public ResponseDTO<CreditCardResponseDTO> createCreditCard(CreditCardRequestDTO creditCardRequestDTO){
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();

        String errors = creditCardValidation.validateCreditCardRequestRules(creditCardRequestDTO);

        if(!errors.isEmpty()) {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        if(creditCardRequestDTO.mainCard()){
            List<CreditCard> customerCreditCards = creditCardRepository.findAllByCustomerId(customer.getId());
            if(!customerCreditCards.isEmpty()){
                for(CreditCard card: customerCreditCards){
                    if(card.isMainCard()){
                        card.setMainCard(false);
                        try{
                            creditCardRepository.save(card);
                        }catch(Exception e){
                            return new ResponseDTO<>(
                                    HttpStatus.BAD_REQUEST.toString(),
                                    "Erro ao atualizar dados do cartão, tente novamente mais tarde",
                                    null
                            );
                        }
                    }
                }
            }
        }

        CreditCard card = new CreditCard();

        card.setCardNumber(String.valueOf(creditCardRequestDTO.cardNumber()));
        card.setCardName(creditCardRequestDTO.cardName());
        card.setCardCode(String.valueOf(creditCardRequestDTO.cardCode()));
        card.setCardFlag(creditCardRequestDTO.cardFlag());
        card.setMainCard(creditCardRequestDTO.mainCard());
        card.setCustomer(customer);

        try{
            creditCardRepository.save(card);

            // Garante que o cliente tenha sempre pelo menos um cartão principal
            ensureAtLeastOneMainCard(customer);
        }catch(Exception e){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Erro ao cadastrar dados do cartão, tente novamente mais tarde",
                    null
            );
        }

        return new ResponseDTO<>(
                HttpStatus.CREATED.toString(),
                "Cartão de crédito salvo com sucesso!",
                null
        );
    }

    public ResponseDTO<CreditCardResponseDTO> updateCreditCard(Long id, CreditCardRequestDTO creditCardRequestDTO) {
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();
        Optional<CreditCard> creditCard = creditCardRepository.findByIdAndCustomerId(id, customer.getId());

        String errors = creditCardValidation.validateCreditCardRequestRules(creditCardRequestDTO);

        if(!errors.isEmpty()) {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        if(creditCardRequestDTO.mainCard()){
            List<CreditCard> customerCreditCards = creditCardRepository.findAllByCustomerId(customer.getId());
            if(!customerCreditCards.isEmpty()){
                for(CreditCard card: customerCreditCards){
                    if(card.isMainCard()){
                        card.setMainCard(false);
                        try{
                            creditCardRepository.save(card);
                        }catch(Exception e){
                            return new ResponseDTO<>(
                                    HttpStatus.BAD_REQUEST.toString(),
                                    "Erro ao atualizar dados do cartão, tente novamente mais tarde",
                                    null
                            );
                        }
                    }
                }
            }
        }

        if(creditCard.isEmpty()){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Cartão de crédito não encontrado",
                    null
            );
        }else{
            CreditCard card = creditCard.get();

            card.setId(id);
            card.setCardNumber(String.valueOf(creditCardRequestDTO.cardNumber()));
            card.setCardName(creditCardRequestDTO.cardName());
            card.setCardCode(String.valueOf(creditCardRequestDTO.cardCode()));
            card.setCardFlag(creditCardRequestDTO.cardFlag());
            card.setMainCard(creditCardRequestDTO.mainCard());
            card.setCustomer(customer);

            try{
                creditCardRepository.save(card);

                // Garante que o cliente tenha sempre pelo menos um cartão principal
                ensureAtLeastOneMainCard(customer);
            }catch(Exception e){
                return new ResponseDTO<>(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Erro ao atualizar dados do cartão, tente novamente mais tarde",
                        null
                );
            }
        }

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Cartão de crédito atualizado com sucesso!",
                null
        );
    }

    public ResponseDTO<CreditCardResponseDTO> deleteCreditCard(Long id){
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();
        Optional<CreditCard> creditCard = creditCardRepository.findByIdAndCustomerId(id, customer.getId());

        if(creditCard.isEmpty()){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Cartão de crédito não encontrado",
                    null
            );
        }else{
            try{
                creditCardRepository.deleteById(creditCard.get().getId());

                // Após excluir, garante que, se ainda houver cartões,
                // pelo menos um continue marcado como principal
                ensureAtLeastOneMainCard(customer);

                return new ResponseDTO<>(
                        HttpStatus.OK.toString(),
                        "Cartão de crédito excluído com sucesso!",
                        null
                );
            }catch(Exception e){
                return new ResponseDTO<>(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Erro ao excluir cartão de crédito, tente novamente mais tarde",
                        null
                );
            }
        }
    }

    /**
     * Garante que o cliente tenha sempre pelo menos um cartão principal
     * (quando há pelo menos um cartão cadastrado).
     * Se nenhum cartão estiver marcado como principal, o primeiro da lista é promovido.
     * Também normaliza para haver no máximo um principal.
     */
    private void ensureAtLeastOneMainCard(Customer customer) {
        List<CreditCard> cards = creditCardRepository.findAllByCustomerId(customer.getId());

        if (cards == null || cards.isEmpty()) {
            return; // sem cartões, nada a fazer
        }

        CreditCard firstMain = null;
        for (CreditCard card : cards) {
            if (card.isMainCard()) {
                if (firstMain == null) {
                    firstMain = card;
                } else {
                    // Se por algum motivo houver mais de um principal, normaliza
                    card.setMainCard(false);
                    creditCardRepository.save(card);
                }
            }
        }

        // Se nenhum cartão principal foi encontrado, promove o primeiro da lista
        if (firstMain == null) {
            CreditCard toPromote = cards.get(0);
            toPromote.setMainCard(true);
            creditCardRepository.save(toPromote);
        }
    }

    public ResponseDTO<CreditCardResponseDTO> listAllByCustomer() {
        CustomerAuthDTO customerAuth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerRepository.findById(customerAuth.id()).orElseThrow();
        List<CreditCardResponseDTO> response = new ArrayList<>();

        try{
            List<CreditCard> creditCards = creditCardRepository.findAllByCustomerId(customer.getId());
            if(!creditCards.isEmpty()){
                for(CreditCard creditCard: creditCards){
                    response.add(new CreditCardResponseDTO(
                            creditCard.getId(),
                            creditCard.getCardNumber(),
                            creditCard.getCardName(),
                            creditCard.getCardCode(),
                            creditCard.getCardFlag(),
                            creditCard.isMainCard()
                    ));
                }
            }
            return new ResponseDTO<>(
                    HttpStatus.OK.toString(),
                    "Lista de cartões de crédito resgatada com sucesso!",
                    response
            );
        }catch (Exception e){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Erro ao resgatar lista de cartões.",
                    null
            );
        }
    }

    public ResponseDTO<CreditCardResponseDTO> listAllCreditCard() {
        List<CreditCardResponseDTO> response = new ArrayList<>();

        try{
            List<CreditCard> creditCards = creditCardRepository.findAll();
            if(!creditCards.isEmpty()){
                for(CreditCard creditCard: creditCards){
                    response.add(new CreditCardResponseDTO(
                            creditCard.getId(),
                            creditCard.getCardNumber(),
                            creditCard.getCardName(),
                            creditCard.getCardCode(),
                            creditCard.getCardFlag(),
                            creditCard.isMainCard()
                    ));
                }
            }
            return new ResponseDTO<>(
                    HttpStatus.OK.toString(),
                    "Lista de cartões de crédito resgatada com sucesso!",
                    response
            );
        }catch (Exception e){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Erro ao resgatar lista de cartões.",
                    null
            );
        }
    }
}

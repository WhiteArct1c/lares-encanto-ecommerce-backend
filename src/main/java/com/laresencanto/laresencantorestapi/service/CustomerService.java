package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.*;
import com.laresencanto.laresencantorestapi.dto.request.RegisterRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerUpdateResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CreditCardRepository;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.GenderRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.utils.enums.UserRole;
import com.laresencanto.laresencantorestapi.validation.CreditCardValidation;
import com.laresencanto.laresencantorestapi.validation.UserValidation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final GenderRepository genderRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;
    private final UserValidation userValidation = new UserValidation();
    private final CreditCardValidation creditCardValidation;

    public CustomerService(
            CustomerRepository customerRepository,
            GenderRepository genderRepository,
            TokenService tokenService,
            UserRepository userRepository,
            CreditCardRepository creditCardRepository,
            CreditCardValidation creditCardValidation

    ){
        this.customerRepository = customerRepository;
        this.genderRepository = genderRepository;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.creditCardRepository = creditCardRepository;
        this.creditCardValidation = creditCardValidation;
    }

    public ResponseDTO<CustomerResponseDTO> saveCustomer(CustomerRequestDTO customerRequestDTO){
        String errors = validateCustomerRequestData(customerRequestDTO.user());

        if(!errors.isEmpty()){
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        Customer customer = saveCustomerRequestMapper(customerRequestDTO);
        customer.getUser().setPassword(new BCryptPasswordEncoder().encode(customer.getUser().getPassword()));

        Customer newCustomer = customerRepository.save(customer);

        CustomerResponseDTO response = new CustomerResponseDTO(
                newCustomer.getId(),
                newCustomer.getFullName(),
                newCustomer.getCpf(),
                newCustomer.getBirthDate(),
                newCustomer.getPhone(),
                newCustomer.getGender(),
                newCustomer.getAddress(),
                null
        );

        return new ResponseDTO<>(HttpStatus.CREATED.toString(), "Cliente salvo com sucesso", List.of(response));
    }

    public ResponseDTO<CreditCardResponseDTO> createCreditCard(CreditCardRequestDTO creditCardRequestDTO){
        var decodedToken = tokenService.decodedJwtToken(creditCardRequestDTO.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);

        String errors = creditCardValidation.validateCreditCardRequestRules(creditCardRequestDTO);

        if(!errors.isEmpty()) {
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        if(customer.isPresent()){
            CreditCard card = new CreditCard();

            card.setCardNumber(String.valueOf(creditCardRequestDTO.cardNumber()));
            card.setCardName(creditCardRequestDTO.cardName());
            card.setCardCode(String.valueOf(creditCardRequestDTO.cardCode()));
            card.setCardFlag(creditCardRequestDTO.cardFlag());
            card.setMainCard(creditCardRequestDTO.mainCard());
            card.setCustomer(customer.get());

            try{
                creditCardRepository.save(card);

                return new ResponseDTO<>(
                        HttpStatus.CREATED.toString(),
                        "Cartão de crédito salvo com sucesso!",
                        null
                );
            }catch(Exception e){
                return new ResponseDTO<>(
                        HttpStatus.BAD_REQUEST.toString(),
                        "Erro ao cadastrar dados do cartão, tente novamente mais tarde",
                        null
                );
            }
        }

        return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Erro ao cadastrar dados do cartão, tente novamente mais tarde", null);
    }

    public ResponseDTO<CreditCardResponseDTO> deleteCreditCard(CreditCardRequestDTO creditCardRequestDTO) {
        var decodedToken = tokenService.decodedJwtToken(creditCardRequestDTO.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);

        if(customer.isEmpty()){
            return new ResponseDTO<>(
                    HttpStatus.BAD_REQUEST.toString(),
                    "Usuário inválido ou sessão expirada, tente novamente mais tarde!",
                    null
            );
        }

        try{
            creditCardRepository.deleteById(creditCardRequestDTO.id());
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

    public ResponseDTO<CreditCardResponseDTO> listCreditCard(String token){
        var decodedToken = tokenService.decodedJwtToken(token);
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Optional<Customer> customer = customerRepository.findByUser(user);
        List<CreditCardResponseDTO> response = new ArrayList<>();

        if(customer.isPresent()){
            Optional<List<CreditCard>> creditCards = creditCardRepository.findAllByCustomerId(customer.get().getId());
            if(creditCards.isPresent()){
                for(CreditCard creditCard: creditCards.get()){
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
        }
        return new ResponseDTO<>(
                HttpStatus.BAD_REQUEST.toString(),
                "Erro ao resgatar lista de cartões.",
                null
        );
    }
    public ResponseDTO<CustomerResponseDTO> listAllCostumers(){
        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponseDTO> response = new ArrayList<>();

        for(Customer customer: customers) {
            response.add(
                    new CustomerResponseDTO(
                            customer.getId(),
                            customer.getFullName(),
                            customer.getCpf(),
                            customer.getBirthDate(),
                            customer.getPhone(),
                            customer.getGender(),
                            customer.getAddress(),
                            null
                    )
            );
        }
        return new ResponseDTO<>(HttpStatus.OK.toString(), "Lista de clientes resgatada com sucesso", response);
    }

    public ResponseDTO<CustomerUpdateResponseDTO> update(CustomerUpdateRequestDTO customerUpdateRequestDTO){
        var decodedToken = tokenService.decodedJwtToken(customerUpdateRequestDTO.token());
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        List<CustomerUpdateResponseDTO> response = new ArrayList<>();
        Optional<Customer> optionalCustomer = customerRepository.findByUser(user);
        Gender gender = genderRepository.findByName(customerUpdateRequestDTO.gender().name());

        if(optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();

            customer.setFullName(customerUpdateRequestDTO.fullName());
            customer.setCpf(customerUpdateRequestDTO.cpf());
            customer.setBirthDate(customerUpdateRequestDTO.birthDate());
            customer.setPhone(customerUpdateRequestDTO.phone());
            customer.setGender(gender);

            Customer updatedCustomer = customerRepository.save(customer);

            response.add(
                    new CustomerUpdateResponseDTO(
                            updatedCustomer.getId(),
                            updatedCustomer.getFullName(),
                            updatedCustomer.getCpf(),
                            updatedCustomer.getBirthDate(),
                            updatedCustomer.getPhone(),
                            updatedCustomer.getGender()
                    )
            );

            return new ResponseDTO<>(HttpStatus.OK.toString(), "Dados atualizados com sucesso", response);
        }else{
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Erro ao atualizar os dados, por favor, tente novamente mais tarde", null);
        }
    }

    private Customer saveCustomerRequestMapper(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        Gender gender = genderRepository.findByName(customerRequestDTO.gender().name());
        User user = new User();
        Address addressCustomer = new Address();
        Set<Address> addresses = new HashSet<>();

        gender.setName(customerRequestDTO.gender().toString());

        customer.setFullName(customerRequestDTO.fullName());
        customer.setCpf(customerRequestDTO.cpf());
        customer.setBirthDate(customerRequestDTO.birthDate());
        customer.setPhone(customerRequestDTO.phone());
        customer.setGender(gender);
        customer.setRanking("0"); //default ranking

        user.setEmail(customerRequestDTO.user().email());
        user.setPassword(customerRequestDTO.user().password());
        user.setRole(UserRole.USER); //sempre será usuários comuns
        user.setIsActive("1");

        addressCustomer.setTitle(customerRequestDTO.address().title());
        addressCustomer.setCep(customerRequestDTO.address().cep());
        addressCustomer.setResidenceType(customerRequestDTO.address().residenceType());
        addressCustomer.setAddressType(customerRequestDTO.address().addressType());
        addressCustomer.setStreetName(customerRequestDTO.address().streetName());
        addressCustomer.setAddressNumber(customerRequestDTO.address().addressNumber());
        addressCustomer.setNeighborhoods(customerRequestDTO.address().neighborhoods());
        addressCustomer.setCity(customerRequestDTO.address().city());
        addressCustomer.setState(customerRequestDTO.address().state());
        addressCustomer.setCountry(customerRequestDTO.address().country());
        addressCustomer.setObservations(customerRequestDTO.address().observations());

        addresses.add(addressCustomer);

        customer.setUser(user);
        customer.setAddress(addresses);

        return customer;
    }

    private String validateCustomerRequestData(RegisterRequestDTO registerRequestDTO){
        return userValidation.validateCustomerRequestRules(registerRequestDTO);
    }


}

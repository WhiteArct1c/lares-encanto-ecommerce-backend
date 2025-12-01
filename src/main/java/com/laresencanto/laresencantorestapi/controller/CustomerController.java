package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseAdminDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerUpdateResponseDTO;
import com.laresencanto.laresencantorestapi.exception.CustomerNotFoundException;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.service.CustomerService;
import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.address.Address;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public CustomerController(
            CustomerService customerService
            , TokenService tokenService
            , UserRepository userRepository
            , CustomerRepository customerRepository
    ){
        this.customerService = customerService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/self")
    public ResponseEntity<ResponseDTO<CustomerAuthDTO>> getCustomerById() throws CustomerNotFoundException {
        CustomerAuthDTO auth = (CustomerAuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Recarrega o cliente do banco para garantir que endereços/cartões estejam atualizados
        Customer customer = customerRepository.findById(auth.id())
                .orElseThrow(() -> new CustomerNotFoundException("Cliente não encontrado"));

        List<AddressRequestDTO> addresses = customer.getAddress()
                .stream()
                .filter(Address::getIsActive)
                .map(address -> new AddressRequestDTO(
                        address.getId() != null ? address.getId().toString() : null,
                        address.getTitle(),
                        address.getCep(),
                        address.getResidenceType(),
                        address.getAddressType(),
                        address.getCategories() != null
                                ? address.getCategories().stream().map(AddressCategory::getCategory).toList()
                                : List.of(),
                        address.getStreetName(),
                        address.getAddressNumber(),
                        address.getNeighborhoods(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getObservations(),
                        true // endereços retornados aqui são sempre do cadastro do cliente
                ))
                .toList();

        List<CreditCardResponseDTO> creditCards = customer.getCreditCardList()
                .stream()
                .map(creditCard -> new CreditCardResponseDTO(
                        creditCard.getId(),
                        creditCard.getCardNumber(),
                        creditCard.getCardName(),
                        creditCard.getCardCode(),
                        creditCard.getCardFlag(),
                        creditCard.isMainCard()
                ))
                .toList();

        CustomerAuthDTO upToDate = new CustomerAuthDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getCpf(),
                customer.getBirthDate(),
                customer.getPhone(),
                customer.getGender(),
                customer.getRanking(),
                addresses,
                creditCards
        );

        return ResponseEntity.ok(
                new ResponseDTO<>(
                        HttpStatus.OK.toString(),
                        "Cliente encontrado com sucesso",
                        List.of(upToDate)
                )
        );
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponseAdminDTO>> getAllCustomers(@PageableDefault(sort = {"id"}) Pageable pageable) throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.listAllCustomers(pageable));
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<CustomerUpdateResponseDTO>> updateCustomer(@RequestBody @Valid CustomerUpdateRequestDTO customerData){
        ResponseDTO<CustomerUpdateResponseDTO> response = customerService.update(customerData);
        return ResponseEntity.ok(response);
    }

}

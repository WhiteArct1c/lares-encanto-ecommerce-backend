package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerCreateCardRequest;
import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerUpdateResponseDTO;
import com.laresencanto.laresencantorestapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService
    ){
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllCustomers(){
        ResponseDTO response = customerService.listAllCostumers();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<CustomerUpdateResponseDTO>> updateCustomer(@RequestBody @Valid CustomerUpdateRequestDTO customerData){
        ResponseDTO<CustomerUpdateResponseDTO> response = customerService.update(customerData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-credit-card")
    public ResponseDTO<CreditCardResponseDTO> createCreditCard(@RequestBody @Valid CreditCardRequestDTO creditCardRequestDTO){
        return customerService.createCreditCard(creditCardRequestDTO);
    }

    @PostMapping("/delete-credit-card")
    public ResponseDTO<CreditCardResponseDTO> deleteCreditCard(@RequestBody @Valid CreditCardRequestDTO creditCardRequestDTO){
        return customerService.deleteCreditCard(creditCardRequestDTO);
    }

    @GetMapping("/list-credit-card")
    public ResponseDTO<CreditCardResponseDTO> listCreditCard(@RequestHeader(name="Authorization") String token){
        if (token != null && token.startsWith("Bearer ")) {
            return customerService.listCreditCard(token.substring(7));
        }
        return new ResponseDTO<>(
                HttpStatus.UNAUTHORIZED.toString(),
                "Acesso não autorizado ou token expirado.",
                null
        );
    }
}

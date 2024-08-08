package com.laresencanto.laresencantorestapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laresencanto.laresencantorestapi.dto.request.customer.CreditCardRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.service.CreditCardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/credit-cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping("/{id}")
    public ResponseDTO<CreditCardResponseDTO> getCreditCardById(@PathVariable Long id){
        return creditCardService.getCreditCardById(id);
    }

    @GetMapping
    public ResponseDTO<CreditCardResponseDTO> listAllByCustomer() {
        return creditCardService.listAllByCustomer();
    }

    @PostMapping
    public ResponseDTO<CreditCardResponseDTO> createCreditCard(@RequestBody @Valid CreditCardRequestDTO creditCardRequestDTO){
        return creditCardService.createCreditCard(creditCardRequestDTO);
    }

    @PutMapping("/{id}")
    public ResponseDTO<CreditCardResponseDTO> updateCreditCard(@PathVariable Long id, @RequestBody @Valid CreditCardRequestDTO creditCardRequestDTO){
        return creditCardService.updateCreditCard(id, creditCardRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<CreditCardResponseDTO> deleteCreditCard(@PathVariable Long id) {
        return creditCardService.deleteCreditCard(id);
    }
}

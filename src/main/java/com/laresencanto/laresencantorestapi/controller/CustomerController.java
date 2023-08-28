package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registerCustomer(@RequestBody @Valid CustomerRequestDTO data){
        ResponseDTO response = customerService.saveCustomer(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public String getDummy(){
        return "Hello world";
    }
}

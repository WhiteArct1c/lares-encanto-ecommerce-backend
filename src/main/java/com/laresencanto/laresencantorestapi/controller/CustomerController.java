package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.service.CustomerService;
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
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequestDTO data){
        Customer savedCustomer = customerService.saveCustomer(data);
        return ResponseEntity.ok(savedCustomer);
    }

    @GetMapping
    public String getDummy(){
        return "vai toma no cu";
    }
}

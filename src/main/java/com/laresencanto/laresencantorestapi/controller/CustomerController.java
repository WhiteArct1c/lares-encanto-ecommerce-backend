package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerUpdateRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseAdminDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerUpdateResponseDTO;
import com.laresencanto.laresencantorestapi.exception.CustomerNotFoundException;
import com.laresencanto.laresencantorestapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<CustomerResponseAdminDTO>> getAllCustomers(@PageableDefault(sort = {"id"}) Pageable pageable) throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.listAllCustomers(pageable));
    }

    @PutMapping
    public ResponseEntity<ResponseDTO<CustomerUpdateResponseDTO>> updateCustomer(@RequestBody @Valid CustomerUpdateRequestDTO customerData){
        ResponseDTO<CustomerUpdateResponseDTO> response = customerService.update(customerData);
        return ResponseEntity.ok(response);
    }

}

package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.costumer.CustomerDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @PostMapping
    public CustomerDTO createUser(@RequestBody CustomerDTO data){
        return data;
    }
}

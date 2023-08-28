package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.domain.Address;
import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.Gender;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.mappers.CustomerMapper;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.strategy.IStrategy;
import com.laresencanto.laresencantorestapi.strategy.impl.user.ValidatePassword;
import com.laresencanto.laresencantorestapi.utils.enums.UserRole;
import com.laresencanto.laresencantorestapi.validation.CustomerValidation;
import com.laresencanto.laresencantorestapi.validation.UserValidation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerValidation customerValidation = new CustomerValidation();
    private final UserValidation userValidation = new UserValidation();

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public ResponseDTO<CustomerResponseDTO> saveCustomer(CustomerRequestDTO customerRequestDTO){
        String errors = validateCustomerRequestData(customerRequestDTO);

        if(!errors.isEmpty()){
            return new ResponseDTO<CustomerResponseDTO>(HttpStatus.BAD_REQUEST.toString(), errors, null);
        }

        Customer customer = CustomerMapper.saveCustomerRequestMapper(customerRequestDTO);
        customer.getUser().setPassword(new BCryptPasswordEncoder().encode(customer.getUser().getPassword()));

        try{
            customerRepository.save(customer);
        }catch(Exception e){
            return new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null);
        }

        CustomerResponseDTO response = new CustomerResponseDTO(
                customer.getFullName(),
                customer.getCpf(),
                customer.getBirthDate(),
                customer.getPhone()
        );

        return new ResponseDTO<>(HttpStatus.CREATED.toString(), "Cliente salvo com sucesso", Arrays.asList(response));
    }

    private String validateCustomerRequestData(CustomerRequestDTO customerRequestDTO){
        StringBuilder errors = new StringBuilder();

        errors.append(userValidation.validateRules(customerRequestDTO));
        errors.append(customerValidation.validateRules(customerRequestDTO));

        return errors.toString();
    }

}

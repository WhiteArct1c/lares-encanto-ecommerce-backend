package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.AuthenticationDTO;
import com.laresencanto.laresencantorestapi.dto.request.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.login.LoginResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CustomerService customerService;

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            CustomerService customerService
    ){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(
                new ResponseDTO<>(
                        HttpStatus.OK.toString(),
                        "Login efetuado com sucesso!",
                        List.of(new LoginResponseDTO(token))
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid CustomerRequestDTO data){
        ResponseDTO response = customerService.saveCustomer(data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ResponseDTO> validate(@RequestBody @Valid String token){
        var decodedToken = tokenService.decodedJwtToken(token);
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());
        Customer customer = customerRepository.findByUser(user);

        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getCpf(),
                customer.getBirthDate(),
                customer.getPhone(),
                customer.getGender(),
                customer.getAddress()
        );

        ResponseDTO response = new ResponseDTO(HttpStatus.OK.toString(), "Usuário validado com sucesso!", List.of(customerResponseDTO));

        return ResponseEntity.ok(response);
    }
}

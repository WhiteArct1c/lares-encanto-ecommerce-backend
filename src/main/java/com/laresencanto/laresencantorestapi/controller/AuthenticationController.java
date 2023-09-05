package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.dto.request.AuthenticationDTO;
import com.laresencanto.laresencantorestapi.dto.request.customer.CustomerRequestDTO;
import com.laresencanto.laresencantorestapi.dto.request.user.UpdatePasswordRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CustomerResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.error.ResponseErrorDTO;
import com.laresencanto.laresencantorestapi.dto.response.login.LoginResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.security.TokenService;
import com.laresencanto.laresencantorestapi.service.AuthorizationService;
import com.laresencanto.laresencantorestapi.service.CustomerService;
import com.laresencanto.laresencantorestapi.service.UserService;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserService userService,
            TokenService tokenService,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            CustomerService customerService
    ){
        this.authenticationManager = authenticationManager;
        this.userService = userService;
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

        var decodedToken = tokenService.decodedJwtToken(token);
        User user = (User) userRepository.findByEmail(decodedToken.getSubject());

        if(user.getIsActive().equals("1")){
            return ResponseEntity.ok(
                    new ResponseDTO<>(
                            HttpStatus.OK.toString(),
                            "Login efetuado com sucesso!",
                            List.of(new LoginResponseDTO(token))
                    )
            );
        }else{
            return ResponseEntity.ok(
                    new ResponseErrorDTO(
                            HttpStatus.BAD_REQUEST.toString(),
                            "Usuário está inativado! Consulte o administrador do sistema.",
                            null
                    )
            );
        }


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
        Optional<Customer> customer = customerRepository.findByUser(user);
        ResponseDTO<CustomerResponseDTO> response;

        if(customer.isPresent()){
            CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(
                    customer.get().getId(),
                    customer.get().getFullName(),
                    customer.get().getCpf(),
                    customer.get().getBirthDate(),
                    customer.get().getPhone(),
                    customer.get().getGender(),
                    customer.get().getAddress()
            );

             response = new ResponseDTO<>(HttpStatus.OK.toString(), "Usuário validado com sucesso!", List.of(customerResponseDTO));
            return ResponseEntity.ok(response);
        }else{
            response = new ResponseDTO<>(HttpStatus.BAD_REQUEST.toString(), "Erro ao validar usuário!", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/deactivate-account")
    public ResponseEntity<ResponseDTO> inactivateAccount(@RequestBody @Valid String token){
        try{
            var decodedToken = tokenService.decodedJwtToken(token);
            User user = (User) userRepository.findByEmail(decodedToken.getSubject());
            user.setIsActive("0");
            userRepository.save(user);
            ResponseDTO response = new ResponseDTO(HttpStatus.OK.toString(), "Conta inativada com sucesso!", null);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            ResponseDTO response = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Erro ao desativar conta!", null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

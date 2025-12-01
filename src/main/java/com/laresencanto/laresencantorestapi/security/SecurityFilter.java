package com.laresencanto.laresencantorestapi.security;

import com.laresencanto.laresencantorestapi.domain.customer.Customer;
import com.laresencanto.laresencantorestapi.domain.address.Address;
import com.laresencanto.laresencantorestapi.domain.user.User;
import com.laresencanto.laresencantorestapi.dto.CustomerAuthDTO;
import com.laresencanto.laresencantorestapi.dto.request.address.AddressRequestDTO;
import com.laresencanto.laresencantorestapi.dto.response.customer.CreditCardResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import com.laresencanto.laresencantorestapi.utils.enums.AddressCategory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public SecurityFilter(
            TokenService tokenService,
            UserRepository userRepository, CustomerRepository customerRepository
    ){
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if(token != null){
            try {
                var email = tokenService.validateToken(token);
                User user = userRepository.findByEmail(email);
                Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow();

                var authentication = getUsernamePasswordAuthenticationToken(customer, user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.sendError(403, "Invalid token");
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

    protected UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(Customer customer, User user) {
        List<AddressRequestDTO> addresses = customer.getAddress()
                .stream()
                .filter(Address::getIsActive)
                .map(address -> new AddressRequestDTO(
                        address.getId().toString(),
                        address.getTitle(),
                        address.getCep(),
                        address.getResidenceType(),
                        address.getAddressType(),
                        address.getCategories().stream().map(AddressCategory::getCategory).toList(),
                        address.getStreetName(),
                        address.getAddressNumber(),
                        address.getNeighborhoods(),
                        address.getCity(),
                        address.getState(),
                        address.getCountry(),
                        address.getObservations(),
                        true // endereços retornados do cadastro sempre pertencem ao address book
                )).toList();

        List<CreditCardResponseDTO> creditCards = customer.getCreditCardList()
                .stream()
                .map(creditCard -> new CreditCardResponseDTO(
                        creditCard.getId(),
                        creditCard.getCardNumber(),
                        creditCard.getCardName(),
                        creditCard.getCardCode(),
                        creditCard.getCardFlag(),
                        creditCard.isMainCard()
                )).toList();

        CustomerAuthDTO customerAuthDTO = new CustomerAuthDTO(
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

        return new UsernamePasswordAuthenticationToken(customerAuthDTO, null, user.getAuthorities());
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}

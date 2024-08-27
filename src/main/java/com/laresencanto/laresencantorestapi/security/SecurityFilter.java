package com.laresencanto.laresencantorestapi.security;

import com.laresencanto.laresencantorestapi.domain.Customer;
import com.laresencanto.laresencantorestapi.domain.User;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
                Customer customer = customerRepository.findByUserId(user.getId()).orElse(null);

                var authentication = new UsernamePasswordAuthenticationToken(customer, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.sendError(403, "Invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}

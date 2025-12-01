package com.laresencanto.laresencantorestapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthenticationConfiguration authConfiguration;
    private final SecurityFilter securityFilter;

    public WebSecurityConfig(AuthenticationConfiguration authConfiguration, SecurityFilter securityFilter) {
        this.authConfiguration = authConfiguration;
        this.securityFilter = securityFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    configureAuthEndpoints(authorize);
                    configureCustomerEndpoints(authorize);
                    configureCreditCardEndpoints(authorize);
                    configureProductEndpoints(authorize);
                    configurePricingGroupEndpoints(authorize);
                    authorize.anyRequest().permitAll(); // Libera qualquer outra requisição
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void configureAuthEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll();
    }

    private void configureCustomerEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.GET, "/customers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/customers").hasAnyRole("ADMIN", "USER");
    }

    private void configureCreditCardEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.GET, "/credit-card/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/credit-card/list-all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/credit-card/create-card").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/credit-card/update-card").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/credit-card/delete-card/{id}").hasRole("USER");
    }

    private void configureProductEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.GET, "/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/products/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/products/enable").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/products/disable").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/products/search-by-image").permitAll();
    }

    private void configurePricingGroupEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(HttpMethod.GET, "/pricing-groups").hasRole("ADMIN");
    }
}

package com.laresencanto.laresencantorestapi.dto.request;


public record RegisterRequestDTO(
        String email,
        String password,
        String confirmedPassword
) {
}

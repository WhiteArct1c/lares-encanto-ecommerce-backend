package com.laresencanto.laresencantorestapi.dto.request.user;


public record UserRequestDTO(
        String email,
        String password,
        String confirmedPassword
) {
}

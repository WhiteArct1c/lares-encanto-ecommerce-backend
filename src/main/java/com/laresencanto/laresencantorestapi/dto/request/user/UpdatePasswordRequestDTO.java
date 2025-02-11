package com.laresencanto.laresencantorestapi.dto.request.user;

public record UpdatePasswordRequestDTO(
        String token,
        String password,
        String confirmedPassword
) {
}

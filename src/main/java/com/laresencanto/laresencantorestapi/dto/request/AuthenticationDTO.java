package com.laresencanto.laresencantorestapi.dto.request;

public record AuthenticationDTO(
        String email,
        String password
) {
}

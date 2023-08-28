package com.laresencanto.laresencantorestapi.dto.response.error;

public record ResponseErrorDTO (
        String code,
        String message,
        String description
){
}

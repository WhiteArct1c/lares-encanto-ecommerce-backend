package com.laresencanto.laresencantorestapi.dto.response;

import java.util.List;

public record ResponseDTO<T>(
        String code,
        String message,
        List<T> data
) {
}

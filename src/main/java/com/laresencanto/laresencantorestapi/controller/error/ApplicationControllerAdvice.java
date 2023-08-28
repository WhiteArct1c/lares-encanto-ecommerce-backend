package com.laresencanto.laresencantorestapi.controller.error;

import com.laresencanto.laresencantorestapi.dto.response.error.ResponseErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleInvalidRequestArgment(MethodArgumentNotValidException ex){
        return new ResponseErrorDTO(
            ex.getStatusCode().toString(),
            ex.getFieldError().getDefaultMessage(),
            ex.getDetailMessageArguments().toString()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseErrorDTO handleBadCredentialsException(BadCredentialsException ex){
        return new ResponseErrorDTO(
            HttpStatus.FORBIDDEN.toString(),
            ex.getMessage(),
            null
        );
    }
}

package com.laresencanto.laresencantorestapi.controller.error;

import com.laresencanto.laresencantorestapi.dto.response.error.ResponseErrorDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleInvalidRequestArgument(MethodArgumentNotValidException ex){
        return new ResponseErrorDTO(
            ex.getStatusCode().toString(),
            ex.getFieldError().getDefaultMessage(),
            null
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

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseErrorDTO handleNonExistentEmail(InternalAuthenticationServiceException ex){
        return new ResponseErrorDTO(
                HttpStatus.FORBIDDEN.toString(),
                "Usuário inexistente ou senha inválida",
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleMissingRequiredFields(DataIntegrityViolationException ex){
        return new ResponseErrorDTO(
                HttpStatus.BAD_REQUEST.toString(),
                "Existe algum campo obrigatório que não foi informado.",
                null
        );
    }
}

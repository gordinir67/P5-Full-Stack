package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.common.SimpleMessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<SimpleMessageResponse> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new SimpleMessageResponse(ex.getReason()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SimpleMessageResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new SimpleMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }

        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponse("Validation error", errors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<SimpleMessageResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(401)
                .body(new SimpleMessageResponse("Unauthorized"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleMessageResponse> handleOther(Exception ex) {
        return ResponseEntity.status(500)
                .body(new SimpleMessageResponse("Internal server error"));
    }
}
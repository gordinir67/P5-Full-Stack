package com.openclassrooms.mddapi.exception;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {

    private final String message;
    private final Map<String, String> errors;

}
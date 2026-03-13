package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.common.SimpleMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResponseStatus_shouldReturnStatusAndMessage() {
        var response = handler.handleResponseStatus(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isInstanceOf(SimpleMessageResponse.class);
        assertThat(((SimpleMessageResponse) response.getBody()).getMessage()).isEqualTo("Not found");
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        var response = handler.handleIllegalArgument(new IllegalArgumentException("Invalid data"));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid data");
    }

    @Test
    void handleValidation_shouldReturnValidationErrors() throws Exception {
        Dummy dummy = new Dummy();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dummy, "dummy");
        bindingResult.addError(new FieldError("dummy", "email", "must be valid"));
        bindingResult.addError(new FieldError("dummy", "password", "must not be blank"));

        Method method = Dummy.class.getDeclaredMethod("setValue", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation error");
        assertThat(response.getBody().getErrors()).containsEntry("email", "must be valid");
        assertThat(response.getBody().getErrors()).containsEntry("password", "must not be blank");
    }

    @Test
    void handleAuthentication_shouldReturnUnauthorized() {
        var response = handler.handleAuthentication(new BadCredentialsException("bad"));

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Unauthorized");
    }

    @Test
    void handleOther_shouldReturnInternalServerError() {
        var response = handler.handleOther(new RuntimeException("boom"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
    }

    static class Dummy {
        public void setValue(String value) {
        }
    }
}
package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.user.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.user.UserDto;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.security.JwtAuthenticationFilter;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ThemeService;
import com.openclassrooms.mddapi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = MeController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class MeControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean UserService userService;
    @MockitoBean ThemeService themeService;
    @MockitoBean JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserPrincipal principal() {
        return new UserPrincipal(1L, "user@example.com", "pwd");
    }

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void getMe_shouldReturnCurrentUser() throws Exception {
        UserDto dto = new UserDto(
                1L,
                "user@example.com",
                "Alice",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(userService.getMe(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/me").with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void updateMe_shouldReturnBadRequestWhenPasswordInvalid() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("weak");

        mockMvc.perform(put("/api/me")
                        .with(user(principal()))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void updateMe_shouldReturnUpdatedUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("user@example.com");
        request.setName("Alice");
        request.setPassword("Password1!");

        UserDto dto = new UserDto(
                1L,
                "user@example.com",
                "Alice",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(userService.updateMe(eq(1L), any(UpdateUserRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/me")
                        .with(user(principal()))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }
}
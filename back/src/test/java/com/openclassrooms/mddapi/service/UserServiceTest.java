package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.user.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.user.UserDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    @Test
    void getMe_shouldReturnMappedUser() {
        User user = User.builder().id(1L).email("user@example.com").name("Alice").password("pwd").build();
        UserDto dto = new UserDto(1L, "user@example.com", "Alice", LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getMe(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getMe_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMe(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updateMe_shouldThrowConflictWhenEmailAlreadyExists() {
        User user = User.builder().id(1L).email("old@example.com").name("Alice").password("pwd").build();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateMe(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void updateMe_shouldTrimFieldsEncodePasswordAndSave() {
        User user = User.builder().id(1L).email("old@example.com").name("Alice").password("pwd").build();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail(" NEW@example.com ");
        request.setName("  Bob  ");
        request.setPassword("Password1!");
        User saved = User.builder().id(1L).email("new@example.com").name("Bob").password("encoded").build();
        UserDto dto = new UserDto(1L, "new@example.com", "Bob", null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(dto);

        UserDto result = userService.updateMe(1L, request);

        assertThat(result).isEqualTo(dto);
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("new@example.com") &&
                        u.getName().equals("Bob") &&
                        u.getPassword().equals("encoded")));
    }
}

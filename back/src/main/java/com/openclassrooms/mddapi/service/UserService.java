package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.user.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.user.UserDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        return userMapper.toDto(user);
    }

    public UserDto updateMe(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim().toLowerCase();

            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
            }

            user.setEmail(newEmail);
        }

        if (request.getName() != null) {
            user.setName(request.getName().trim());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toDto(userRepository.save(user));
    }
}
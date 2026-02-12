package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.auth.AuthResponse;
import com.openclassrooms.mddapi.dto.auth.LoginRequest;
import com.openclassrooms.mddapi.dto.auth.RegisterRequest;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtService;
import com.openclassrooms.mddapi.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }

        User user = User.builder()
                .email(email)
                .name(request.getName().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(UserPrincipal.from(saved));
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtService.generateToken(principal);
        return new AuthResponse(token);
    }
}
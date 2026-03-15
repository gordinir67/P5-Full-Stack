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

/**
 * Service métier chargé de l'inscription et de la connexion des utilisateurs.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Construit le service d'authentification.
     *
     * @param userRepository dépôt des utilisateurs
     * @param passwordEncoder encodeur de mots de passe
     * @param authenticationManager gestionnaire d'authentification Spring Security
     * @param jwtService service de génération des jetons JWT
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Inscrit un nouvel utilisateur puis retourne un jeton JWT.
     *
     * @param request données d'inscription
     * @return réponse d'authentification contenant le jeton
     */
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

    /**
     * Authentifie un utilisateur puis retourne un jeton JWT.
     *
     * @param request données de connexion
     * @return réponse d'authentification contenant le jeton
     */
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

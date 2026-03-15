package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.auth.AuthResponse;
import com.openclassrooms.mddapi.dto.auth.LoginRequest;
import com.openclassrooms.mddapi.dto.auth.RegisterRequest;
import com.openclassrooms.mddapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur d'authentification pour l'inscription et la connexion des utilisateurs.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Construit le contrôleur d'authentification.
     *
     * @param authService service métier d'authentification
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Inscrit un nouvel utilisateur et retourne un jeton JWT.
     *
     * @param request données d'inscription validées
     * @return réponse contenant le jeton JWT
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Authentifie un utilisateur existant et retourne un jeton JWT.
     *
     * @param request données de connexion validées
     * @return réponse contenant le jeton JWT
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}

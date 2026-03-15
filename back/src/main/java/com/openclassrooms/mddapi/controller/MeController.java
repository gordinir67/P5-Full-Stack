package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.subscription.SubscriptionDto;
import com.openclassrooms.mddapi.dto.user.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.user.UserDto;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ThemeService;
import com.openclassrooms.mddapi.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST exposant les actions liées au profil de l'utilisateur connecté.
 */
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/me")
public class MeController {

    private final UserService userService;
    private final ThemeService themeService;

    /**
     * Construit le contrôleur du profil utilisateur.
     *
     * @param userService service de gestion du profil
     * @param themeService service de gestion des abonnements
     */
    public MeController(UserService userService, ThemeService themeService) {
        this.userService = userService;
        this.themeService = themeService;
    }

    /**
     * Retourne le profil de l'utilisateur connecté.
     *
     * @param principal utilisateur authentifié
     * @return données du profil courant
     */
    @GetMapping
    public UserDto getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getMe(principal.getId());
    }

    /**
     * Met à jour le profil de l'utilisateur connecté.
     *
     * @param principal utilisateur authentifié
     * @param request données de mise à jour validées
     * @return profil mis à jour
     */
    @PutMapping
    public UserDto updateMe(@AuthenticationPrincipal UserPrincipal principal,
                            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateMe(principal.getId(), request);
    }

    /**
     * Retourne la liste des abonnements de l'utilisateur connecté.
     *
     * @param principal utilisateur authentifié
     * @return liste des abonnements courants
     */
    @GetMapping("/subscriptions")
    public List<SubscriptionDto> mySubscriptions(@AuthenticationPrincipal UserPrincipal principal) {
        return themeService.listMySubscriptions(principal.getId());
    }
}

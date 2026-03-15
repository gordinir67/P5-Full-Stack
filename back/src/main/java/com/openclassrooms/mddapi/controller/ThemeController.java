package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ThemeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST de consultation des thèmes et de gestion des abonnements.
 */
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    /**
     * Construit le contrôleur des thèmes.
     *
     * @param themeService service métier des thèmes
     */
    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    /**
     * Retourne la liste des thèmes avec l'état d'abonnement de l'utilisateur.
     *
     * @param principal utilisateur authentifié
     * @return liste des thèmes disponibles
     */
    @GetMapping
    public List<ThemeDto> list(@AuthenticationPrincipal UserPrincipal principal) {
        return themeService.listThemes(principal.getId());
    }

    /**
     * Abonne l'utilisateur connecté à un thème.
     *
     * @param principal utilisateur authentifié
     * @param id identifiant du thème
     */
    @PostMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Integer id) {
        themeService.subscribe(principal.getId(), id);
    }

    /**
     * Désabonne l'utilisateur connecté d'un thème.
     *
     * @param principal utilisateur authentifié
     * @param id identifiant du thème
     */
    @DeleteMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Integer id) {
        themeService.unsubscribe(principal.getId(), id);
    }
}

package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ThemeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public List<ThemeDto> list(@AuthenticationPrincipal UserPrincipal principal) {
        return themeService.listThemes(principal.getId());
    }

    @PostMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Integer id) {
        themeService.subscribe(principal.getId(), id);
    }

    @DeleteMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Integer id) {
        themeService.unsubscribe(principal.getId(), id);
    }
}
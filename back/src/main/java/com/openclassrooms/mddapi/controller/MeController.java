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

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/me")
public class MeController {

    private final UserService userService;
    private final ThemeService themeService;

    public MeController(UserService userService, ThemeService themeService) {
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping
    public UserDto getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getMe(principal.getId());
    }

    @PutMapping
    public UserDto updateMe(@AuthenticationPrincipal UserPrincipal principal,
                            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateMe(principal.getId(), request);
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionDto> mySubscriptions(@AuthenticationPrincipal UserPrincipal principal) {
        return themeService.listMySubscriptions(principal.getId());
    }
}
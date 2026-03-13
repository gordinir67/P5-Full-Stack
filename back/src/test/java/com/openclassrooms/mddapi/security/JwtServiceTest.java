package com.openclassrooms.mddapi.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("abcdefghijklmnopqrstuvwxyz123456", 3600);

    @Test
    void shouldGenerateTokenAndExtractClaims() {
        UserPrincipal principal = new UserPrincipal(42L, "user@example.com", "pwd");

        String token = jwtService.generateToken(principal);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@example.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.isTokenValid(token, "user@example.com")).isTrue();
        assertThat(jwtService.isTokenValid(token, "other@example.com")).isFalse();
    }
}

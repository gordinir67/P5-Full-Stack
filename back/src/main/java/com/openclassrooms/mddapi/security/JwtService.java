package com.openclassrooms.mddapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Service utilitaire de génération et de validation des jetons JWT.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;

    /**
     * Construit le service JWT à partir de la configuration applicative.
     *
     * @param secret secret HMAC utilisé pour signer les jetons
     * @param expirationSeconds durée de validité d'un jeton en secondes
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds:86400}") long expirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Génère un jeton JWT pour l'utilisateur authentifié.
     *
     * @param principal utilisateur authentifié
     * @return jeton JWT signé
     */
    public String generateToken(UserPrincipal principal) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("uid", principal.getId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur contenu dans un jeton.
     *
     * @param token jeton JWT
     * @return nom d'utilisateur stocké dans le sujet du jeton
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrait l'identifiant utilisateur contenu dans un jeton.
     *
     * @param token jeton JWT
     * @return identifiant utilisateur ou {@code null} s'il est absent ou invalide
     */
    public Long extractUserId(String token) {
        Object uid = parseClaims(token).get("uid");
        if (uid == null) {
            return null;
        }
        if (uid instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(uid.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Vérifie qu'un jeton n'est pas expiré.
     *
     * @param token jeton JWT
     * @return {@code true} si le jeton est encore valide
     */
    public boolean isTokenValid(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration() != null && claims.getExpiration().after(new Date());
    }

    /**
     * Vérifie qu'un jeton est valide pour un utilisateur attendu.
     *
     * @param token jeton JWT
     * @param expectedUsername nom d'utilisateur attendu
     * @return {@code true} si le jeton correspond à l'utilisateur et n'est pas expiré
     */
    public boolean isTokenValid(String token, String expectedUsername) {
        Claims claims = parseClaims(token);
        return expectedUsername.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
    }

    /**
     * Parse les claims d'un jeton JWT.
     *
     * @param token jeton JWT
     * @return ensemble des claims du jeton
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.openclassrooms.mddapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre Spring Security chargé d'authentifier les requêtes à partir d'un jeton JWT.
 * <p>
 * Le filtre lit l'en-tête {@code Authorization}, valide le jeton Bearer puis reconstruit
 * un {@link UserPrincipal} minimal afin d'alimenter le contexte de sécurité.
 * Si le jeton est absent, invalide ou incomplet, la requête continue sans authentification.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Construit le filtre d'authentification JWT.
     *
     * @param jwtService service utilitaire de validation et d'extraction des informations du jeton
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Tente d'authentifier la requête courante à partir du jeton JWT présent dans l'en-tête HTTP.
     * <p>
     * Si le jeton est valide, un objet d'authentification est placé dans le
     * {@link SecurityContextHolder}. Dans le cas contraire, la chaîne de filtres continue
     * simplement sans utilisateur authentifié.
     *
     * @param request requête HTTP entrante
     * @param response réponse HTTP en cours de construction
     * @param filterChain chaîne des filtres Spring à poursuivre
     * @throws ServletException en cas d'erreur liée au traitement servlet
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractUsername(token);
            Long uid = jwtService.extractUserId(token);

            if (email == null || uid == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // On reconstruit un principal minimal depuis le token
            UserPrincipal principal = new UserPrincipal(uid, email, "");

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, List.of());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            // token invalide => pas d'auth
        }

        filterChain.doFilter(request, response);
    }
}

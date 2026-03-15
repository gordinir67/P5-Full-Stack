package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Représentation de l'utilisateur authentifié dans le contexte Spring Security.
 * <p>
 * Cette classe adapte l'entité métier {@link User} au contrat {@link UserDetails}
 * attendu par les composants de sécurité Spring.
 */
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;

    /**
     * Construit un principal de sécurité à partir des informations minimales de l'utilisateur.
     *
     * @param id identifiant technique de l'utilisateur
     * @param email email utilisé comme nom d'utilisateur
     * @param password mot de passe haché de l'utilisateur
     */
    public UserPrincipal(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    /**
     * Crée un principal Spring Security à partir d'une entité utilisateur.
     *
     * @param user entité métier utilisateur
     * @return principal de sécurité correspondant
     */
    public static UserPrincipal from(User user) {
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword());
    }

    /**
     * Retourne l'identifiant technique de l'utilisateur authentifié.
     *
     * @return identifiant utilisateur
     */
    public Long getId() {
        return id;
    }

    /**
     * Retourne les autorités de l'utilisateur.
     *
     * @return collection des autorités, vide dans l'implémentation actuelle
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Retourne le mot de passe haché de l'utilisateur.
     *
     * @return mot de passe haché
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retourne le nom d'utilisateur utilisé par Spring Security.
     *
     * @return email de l'utilisateur
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indique si le compte utilisateur n'est pas expiré.
     *
     * @return {@code true} dans l'implémentation actuelle
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indique si le compte utilisateur n'est pas verrouillé.
     *
     * @return {@code true} dans l'implémentation actuelle
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indique si les informations d'identification ne sont pas expirées.
     *
     * @return {@code true} dans l'implémentation actuelle
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indique si le compte utilisateur est activé.
     *
     * @return {@code true} dans l'implémentation actuelle
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}

package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implémentation de {@link UserDetailsService} chargée de retrouver un utilisateur
 * à partir de son email pour Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Construit le service de chargement des utilisateurs pour l'authentification.
     *
     * @param userRepository dépôt d'accès aux utilisateurs persistés
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur à partir de son identifiant de connexion.
     * <p>
     * L'identifiant attendu est l'email de l'utilisateur. Celui-ci est normalisé
     * avant recherche afin de fiabiliser l'authentification.
     *
     * @param username email fourni lors de la tentative de connexion
     * @return représentation Spring Security de l'utilisateur trouvé
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à l'email fourni
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username == null ? "" : username.trim().toLowerCase();
        return userRepository.findByEmail(email)
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.subscription.SubscriptionDto;
import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.mapper.SubscriptionMapper;
import com.openclassrooms.mddapi.mapper.ThemeMapper;
import com.openclassrooms.mddapi.models.Subscription;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service métier de gestion des thèmes et des abonnements utilisateur.
 */
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ThemeMapper themeMapper;
    private final SubscriptionMapper subscriptionMapper;

    /**
     * Construit le service des thèmes.
     *
     * @param themeRepository dépôt des thèmes
     * @param subscriptionRepository dépôt des abonnements
     * @param userRepository dépôt des utilisateurs
     * @param themeMapper mapper des thèmes
     * @param subscriptionMapper mapper des abonnements
     */
    public ThemeService(ThemeRepository themeRepository,
                        SubscriptionRepository subscriptionRepository,
                        UserRepository userRepository,
                        ThemeMapper themeMapper,
                        SubscriptionMapper subscriptionMapper) {
        this.themeRepository = themeRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.themeMapper = themeMapper;
        this.subscriptionMapper = subscriptionMapper;
    }

    /**
     * Retourne la liste des thèmes avec l'état d'abonnement d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des thèmes disponibles
     */
    public List<ThemeDto> listThemes(Long userId) {
        return themeRepository.findAll().stream()
                .map(t -> themeMapper.toDto(t, subscriptionRepository.existsByUserIdAndThemeId(userId, t.getId())))
                .toList();
    }

    /**
     * Abonne un utilisateur à un thème.
     *
     * @param userId identifiant de l'utilisateur
     * @param themeId identifiant du thème
     */
    public void subscribe(Long userId, Integer themeId) {
        if (subscriptionRepository.existsByUserIdAndThemeId(userId, themeId)) {
            return;
        }

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thème introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Subscription subscription = Subscription.builder()
                .user(user)
                .theme(theme)
                .build();

        subscriptionRepository.save(subscription);
    }

    /**
     * Désabonne un utilisateur d'un thème.
     *
     * @param userId identifiant de l'utilisateur
     * @param themeId identifiant du thème
     */
    public void unsubscribe(Long userId, Integer themeId) {
        subscriptionRepository.findByUserIdAndThemeId(userId, themeId)
                .ifPresent(subscriptionRepository::delete);
    }

    /**
     * Retourne les abonnements de l'utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @return liste des abonnements
     */
    public List<SubscriptionDto> listMySubscriptions(Long userId) {
        return subscriptionRepository.findAllByUserId(userId).stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }
}

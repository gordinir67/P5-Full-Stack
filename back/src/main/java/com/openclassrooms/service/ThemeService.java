package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.subscription.SubscriptionDto;
import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.exception.ApiException;
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

import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ThemeMapper themeMapper;
    private final SubscriptionMapper subscriptionMapper;

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

    public List<ThemeDto> listThemes(Long userId) {
        return themeRepository.findAll().stream()
                .map(t -> themeMapper.toDto(t, subscriptionRepository.existsByUserIdAndThemeId(userId, t.getId())))
                .toList();
    }

    public void subscribe(Long userId, Integer themeId) {
        if (subscriptionRepository.existsByUserIdAndThemeId(userId, themeId)) {
            return; // idempotent
        }

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Thème introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Subscription subscription = Subscription.builder()
                .user(user)
                .theme(theme)
                .build();

        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(Long userId, Integer themeId) {
        subscriptionRepository.findByUserIdAndThemeId(userId, themeId)
                .ifPresent(subscriptionRepository::delete);
    }

    public List<SubscriptionDto> listMySubscriptions(Long userId) {
        return subscriptionRepository.findAllByUserId(userId).stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }
}
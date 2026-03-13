package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.subscription.SubscriptionDto;
import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.dto.theme.ThemeLightDto;
import com.openclassrooms.mddapi.mapper.SubscriptionMapper;
import com.openclassrooms.mddapi.mapper.ThemeMapper;
import com.openclassrooms.mddapi.models.Subscription;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock ThemeRepository themeRepository;
    @Mock SubscriptionRepository subscriptionRepository;
    @Mock UserRepository userRepository;
    @Mock ThemeMapper themeMapper;
    @Mock SubscriptionMapper subscriptionMapper;

    @InjectMocks ThemeService themeService;

    @Test
    void listThemes_shouldReturnThemesWithSubscriptionFlag() {
        Theme t1 = Theme.builder().id(1).title("Java").description("desc").build();
        Theme t2 = Theme.builder().id(2).title("Spring").description("desc").build();
        when(themeRepository.findAll()).thenReturn(List.of(t1, t2));
        when(subscriptionRepository.existsByUserIdAndThemeId(10L, 1)).thenReturn(true);
        when(subscriptionRepository.existsByUserIdAndThemeId(10L, 2)).thenReturn(false);
        when(themeMapper.toDto(t1, true)).thenReturn(new ThemeDto(1, "Java", "desc", true));
        when(themeMapper.toDto(t2, false)).thenReturn(new ThemeDto(2, "Spring", "desc", false));

        List<ThemeDto> result = themeService.listThemes(10L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isSubscribed()).isTrue();
        assertThat(result.get(1).isSubscribed()).isFalse();
    }

    @Test
    void subscribe_shouldDoNothingWhenAlreadySubscribed() {
        when(subscriptionRepository.existsByUserIdAndThemeId(10L, 3)).thenReturn(true);

        themeService.subscribe(10L, 3);

        verify(themeRepository, never()).findById(any());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void subscribe_shouldSaveSubscription() {
        Theme theme = Theme.builder().id(3).title("Spring").build();
        User user = User.builder().id(10L).email("a@b.com").name("Alice").password("pwd").build();
        when(subscriptionRepository.existsByUserIdAndThemeId(10L, 3)).thenReturn(false);
        when(themeRepository.findById(3)).thenReturn(Optional.of(theme));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        themeService.subscribe(10L, 3);

        verify(subscriptionRepository).save(argThat(sub -> sub.getTheme().equals(theme) && sub.getUser().equals(user)));
    }

    @Test
    void subscribe_shouldThrowWhenThemeDoesNotExist() {
        when(subscriptionRepository.existsByUserIdAndThemeId(10L, 99)).thenReturn(false);
        when(themeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> themeService.subscribe(10L, 99))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Thème introuvable");
                });
    }

    @Test
    void unsubscribe_shouldDeleteWhenSubscriptionExists() {
        Subscription subscription = Subscription.builder().id(5).build();
        when(subscriptionRepository.findByUserIdAndThemeId(10L, 3)).thenReturn(Optional.of(subscription));

        themeService.unsubscribe(10L, 3);

        verify(subscriptionRepository).delete(subscription);
    }

    @Test
    void listMySubscriptions_shouldMapSubscriptions() {
        Subscription sub = Subscription.builder().id(1).theme(Theme.builder().id(3).title("Spring").build()).build();
        SubscriptionDto dto = new SubscriptionDto(1, new ThemeLightDto(3, "Spring"));
        when(subscriptionRepository.findAllByUserId(10L)).thenReturn(List.of(sub));
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        List<SubscriptionDto> result = themeService.listMySubscriptions(10L);

        assertThat(result).containsExactly(dto);
    }
}

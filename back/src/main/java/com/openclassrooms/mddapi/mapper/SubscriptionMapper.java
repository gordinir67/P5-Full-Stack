package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.subscription.SubscriptionDto;
import com.openclassrooms.mddapi.models.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    private final ThemeMapper themeMapper;

    public SubscriptionMapper(ThemeMapper themeMapper) {
        this.themeMapper = themeMapper;
    }

    public SubscriptionDto toDto(Subscription subscription) {
        if (subscription == null) return null;
        return new SubscriptionDto(subscription.getId(), themeMapper.toLight(subscription.getTheme()));
    }
}
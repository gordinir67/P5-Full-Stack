package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.models.Subscription;
import com.openclassrooms.mddapi.models.Theme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionMapperTest {

    private final SubscriptionMapper mapper = new SubscriptionMapper(new ThemeMapper());

    @Test
    void toDto_shouldMapSubscription() {
        Theme theme = new Theme();
        theme.setId(2);
        theme.setTitle("Java");

        Subscription subscription = new Subscription();
        subscription.setId(3);
        subscription.setTheme(theme);

        var result = mapper.toDto(subscription);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getTheme().getTitle()).isEqualTo("Java");
    }

    @Test
    void toDto_shouldReturnNullWhenSubscriptionIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }
}
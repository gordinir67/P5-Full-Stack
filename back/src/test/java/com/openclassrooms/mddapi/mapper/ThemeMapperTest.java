package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.models.Theme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThemeMapperTest {

    private final ThemeMapper mapper = new ThemeMapper();

    @Test
    void toLight_shouldMapTheme() {
        Theme theme = new Theme();
        theme.setId(2);
        theme.setTitle("Java");

        var result = mapper.toLight(theme);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getTitle()).isEqualTo("Java");
    }

    @Test
    void toLight_shouldReturnNullWhenThemeIsNull() {
        assertThat(mapper.toLight(null)).isNull();
    }

    @Test
    void toDto_shouldMapTheme() {
        Theme theme = new Theme();
        theme.setId(2);
        theme.setTitle("Java");
        theme.setDescription("Desc");

        var result = mapper.toDto(theme, true);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Desc");
        assertThat(result.isSubscribed()).isTrue();
    }

    @Test
    void toDto_shouldReturnNullWhenThemeIsNull() {
        assertThat(mapper.toDto(null, false)).isNull();
    }
}
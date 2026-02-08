package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.dto.theme.ThemeLightDto;
import com.openclassrooms.mddapi.models.Theme;
import org.springframework.stereotype.Component;

@Component
public class ThemeMapper {

    public ThemeLightDto toLight(Theme theme) {
        if (theme == null) return null;
        return new ThemeLightDto(theme.getId(), theme.getTitle());
    }

    public ThemeDto toDto(Theme theme, boolean subscribed) {
        if (theme == null) return null;
        return new ThemeDto(
                theme.getId(),
                theme.getTitle(),
                theme.getDescription(),
                subscribed
        );
    }
}
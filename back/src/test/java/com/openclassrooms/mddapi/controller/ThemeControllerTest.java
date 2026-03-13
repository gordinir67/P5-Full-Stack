package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.theme.ThemeDto;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ThemeService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ThemeControllerTest {

    private final ThemeService themeService = mock(ThemeService.class);
    private final ThemeController controller = new ThemeController(themeService);

    private UserPrincipal principal() {
        return new UserPrincipal(1L, "user@example.com", "pwd");
    }

    @Test
    void list_shouldDelegateToService() {
        ThemeDto dto = new ThemeDto(2, "Java", "Desc", true);
        when(themeService.listThemes(1L)).thenReturn(List.of(dto));

        List<ThemeDto> result = controller.list(principal());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java");
        verify(themeService).listThemes(1L);
    }

    @Test
    void subscribe_shouldDelegateToService() {
        controller.subscribe(principal(), 2);

        verify(themeService).subscribe(1L, 2);
    }

    @Test
    void unsubscribe_shouldDelegateToService() {
        controller.unsubscribe(principal(), 2);

        verify(themeService).unsubscribe(1L, 2);
    }
}
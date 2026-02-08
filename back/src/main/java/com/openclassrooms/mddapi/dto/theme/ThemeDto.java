package com.openclassrooms.mddapi.dto.theme;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.subscription.SubscriptionDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDto {
    @NotNull
    private int id;

    @NotBlank
    @Size(max = 50)
    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<ArticleLightDto> articles;

    private List<SubscriptionDto> subscriptions;
}

package com.openclassrooms.mddapi.dto;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.models.Subscription;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
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
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Article> articles;

    private List<Subscription> subscriptions;
}

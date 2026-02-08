package com.openclassrooms.mddapi.dto.article;

import com.openclassrooms.mddapi.dto.theme.ThemeLightDto;
import com.openclassrooms.mddapi.dto.user.UserLightDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLightDto {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    private UserLightDto user;
    private ThemeLightDto theme;
}
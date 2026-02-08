package com.openclassrooms.mddapi.dto.theme;
import com.openclassrooms.mddapi.dto.article.ArticleDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeLightDto {
    private Integer id;

    @NotBlank
    private String title;
}
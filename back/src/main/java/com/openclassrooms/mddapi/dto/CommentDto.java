package com.openclassrooms.mddapi.dto;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.models.User;
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
public class CommentDto {
    private Long id;

    @NotBlank
    @Size(max = 2000)
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Article> articles;

    private List<User> users;
}

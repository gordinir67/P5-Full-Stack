package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleMapperTest {

    private final ArticleMapper mapper = new ArticleMapper(new UserMapper(), new ThemeMapper());

    @Test
    void toLightDto_shouldMapArticle() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        Theme theme = new Theme();
        theme.setId(2);
        theme.setTitle("Java");

        Article article = new Article();
        article.setId(10);
        article.setTitle("Title");
        article.setDescription("Desc");
        article.setCreatedAt(LocalDateTime.now());
        article.setUser(user);
        article.setTheme(theme);

        var result = mapper.toLightDto(article);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getUser().getName()).isEqualTo("Alice");
        assertThat(result.getTheme().getTitle()).isEqualTo("Java");
    }

    @Test
    void toLightDto_shouldReturnNullWhenArticleIsNull() {
        assertThat(mapper.toLightDto(null)).isNull();
    }

    @Test
    void toDto_shouldMapArticleAndComments() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        Theme theme = new Theme();
        theme.setId(2);
        theme.setTitle("Java");

        Article article = new Article();
        article.setId(10);
        article.setTitle("Title");
        article.setDescription("Desc");
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setUser(user);
        article.setTheme(theme);

        CommentDto comment = new CommentDto();
        comment.setId(5);
        comment.setDescription("Nice");

        var result = mapper.toDto(article, List.of(comment));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getDescription()).isEqualTo("Nice");
    }

    @Test
    void toDto_shouldReturnNullWhenArticleIsNull() {
        assertThat(mapper.toDto(null, List.of())).isNull();
    }
}
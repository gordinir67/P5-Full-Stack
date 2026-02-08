package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.article.ArticleDto;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.models.Article;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleMapper {

    private final UserMapper userMapper;
    private final ThemeMapper themeMapper;

    public ArticleMapper(UserMapper userMapper, ThemeMapper themeMapper) {
        this.userMapper = userMapper;
        this.themeMapper = themeMapper;
    }

    public ArticleLightDto toLightDto(Article article) {
        if (article == null) return null;
        return new ArticleLightDto(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                article.getCreatedAt(),
                userMapper.toLight(article.getUser()),
                themeMapper.toLight(article.getTheme())
        );
    }

    public ArticleDto toDto(Article article, List<CommentDto> comments) {
        if (article == null) return null;
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                userMapper.toLight(article.getUser()),
                themeMapper.toLight(article.getTheme()),
                comments
        );
    }
}
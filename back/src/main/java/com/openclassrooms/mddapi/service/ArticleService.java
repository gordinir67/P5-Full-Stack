package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.article.ArticleDto;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.article.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.dto.comment.CreateCommentRequest;
import com.openclassrooms.mddapi.mapper.ArticleMapper;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.models.Article;
import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.Theme;
import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.ArticleRepository;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;

    public ArticleService(ArticleRepository articleRepository,
                          CommentRepository commentRepository,
                          UserRepository userRepository,
                          ThemeRepository themeRepository,
                          ArticleMapper articleMapper,
                          CommentMapper commentMapper) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.themeRepository = themeRepository;
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
    }

    @Transactional(readOnly = true)
    public List<ArticleLightDto> listArticles(String sortDirection) {
        Sort.Direction dir = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return articleRepository.findAll(Sort.by(dir, "createdAt"))
                .stream()
                .map(articleMapper::toLightDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Integer articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));

        List<CommentDto> comments = commentRepository.findAllByArticleIdOrderByCreatedAtAsc(articleId)
                .stream()
                .map(commentMapper::toDto)
                .toList();

        return articleMapper.toDto(article, comments);
    }

    @Transactional
    public ArticleDto createArticle(Long userId, CreateArticleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thème introuvable"));

        Article saved = articleRepository.save(
                Article.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .user(user)
                        .theme(theme)
                        .build()
        );

        return articleMapper.toDto(saved, List.of());
    }

    @Transactional
    public CommentDto addComment(Long userId, Integer articleId, CreateCommentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));

        Comment saved = commentRepository.save(
                Comment.builder()
                        .description(request.getDescription())
                        .user(user)
                        .article(article)
                        .build()
        );

        return commentMapper.toDto(saved);
    }
}
package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.article.ArticleDto;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.article.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.dto.comment.CreateCommentRequest;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<ArticleLightDto> list(@RequestParam(name = "sort", defaultValue = "desc") String sort) {
        return articleService.listArticles(sort);
    }

    @GetMapping("/{id}")
    public ArticleDto get(@PathVariable Integer id) {
        return articleService.getArticle(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto create(@AuthenticationPrincipal UserPrincipal principal,
                             @Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(principal.getId(), request);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@AuthenticationPrincipal UserPrincipal principal,
                                 @PathVariable Integer id,
                                 @Valid @RequestBody CreateCommentRequest request) {
        return articleService.addComment(principal.getId(), id, request);
    }
}
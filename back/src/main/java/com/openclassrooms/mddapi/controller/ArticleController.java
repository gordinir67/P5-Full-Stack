package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.article.ArticleDto;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.article.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.dto.comment.CreateCommentRequest;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ArticleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST de gestion des articles et des commentaires associés.
 */
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * Construit le contrôleur des articles.
     *
     * @param articleService service métier des articles
     */
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * Retourne la liste des articles triés par date de création.
     *
     * @param sort sens du tri ({@code asc} ou {@code desc})
     * @return liste simplifiée des articles
     */
    @GetMapping
    public List<ArticleLightDto> list(@RequestParam(name = "sort", defaultValue = "desc") String sort) {
        return articleService.listArticles(sort);
    }

    /**
     * Retourne le détail d'un article.
     *
     * @param id identifiant de l'article
     * @return article détaillé avec ses commentaires
     */
    @GetMapping("/{id}")
    public ArticleDto get(@PathVariable Integer id) {
        return articleService.getArticle(id);
    }

    /**
     * Crée un nouvel article pour l'utilisateur connecté.
     *
     * @param principal utilisateur authentifié
     * @param request données de création de l'article
     * @return article créé
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto create(@AuthenticationPrincipal UserPrincipal principal,
                             @Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(principal.getId(), request);
    }

    /**
     * Ajoute un commentaire sur un article existant.
     *
     * @param principal utilisateur authentifié
     * @param id identifiant de l'article ciblé
     * @param request données du commentaire
     * @return commentaire créé
     */
    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@AuthenticationPrincipal UserPrincipal principal,
                                 @PathVariable Integer id,
                                 @Valid @RequestBody CreateCommentRequest request) {
        return articleService.addComment(principal.getId(), id, request);
    }
}

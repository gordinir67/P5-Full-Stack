package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.article.ArticleDto;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.article.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.dto.comment.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.theme.ThemeLightDto;
import com.openclassrooms.mddapi.dto.user.UserLightDto;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.security.JwtAuthenticationFilter;
import com.openclassrooms.mddapi.security.SecurityConfig;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.service.ArticleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ArticleController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ArticleControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean ArticleService articleService;
    @MockitoBean JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserPrincipal principal() {
        return new UserPrincipal(1L, "user@example.com", "pwd");
    }

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void list_shouldReturnArticles() throws Exception {
        ArticleLightDto dto = new ArticleLightDto(
                1, "Title", "Desc", LocalDateTime.now(),
                new UserLightDto(1L, "Alice"),
                new ThemeLightDto(2, "Java")
        );
        when(articleService.listArticles("asc")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/articles")
                        .param("sort", "asc")
                        .with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void create_shouldReturnCreatedArticle() throws Exception {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("New article");
        request.setDescription("Description");
        request.setThemeId(2);

        ArticleDto dto = new ArticleDto(
                10, "New article", "Description", null, null,
                new UserLightDto(1L, "Alice"),
                new ThemeLightDto(2, "Java"),
                List.of()
        );

        when(articleService.createArticle(eq(1L), any(CreateArticleRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/articles")
                        .with(user(principal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("New article"));
    }

    @Test
    void addComment_shouldReturnBadRequestWhenInvalid() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setDescription("");

        mockMvc.perform(post("/api/articles/10/comments")
                        .with(user(principal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error"))
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setDescription("Nice article");

        CommentDto dto = new CommentDto(
                4, "Nice article", null, null,
                new UserLightDto(1L, "Alice")
        );

        when(articleService.addComment(eq(1L), eq(10), any(CreateCommentRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/articles/10/comments")
                        .with(user(principal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.description").value("Nice article"));
    }
}
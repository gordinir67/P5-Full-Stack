package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.article.ArticleDto;
import com.openclassrooms.mddapi.dto.article.ArticleLightDto;
import com.openclassrooms.mddapi.dto.article.CreateArticleRequest;
import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.dto.comment.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.theme.ThemeLightDto;
import com.openclassrooms.mddapi.dto.user.UserLightDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock ArticleRepository articleRepository;
    @Mock CommentRepository commentRepository;
    @Mock UserRepository userRepository;
    @Mock ThemeRepository themeRepository;
    @Mock ArticleMapper articleMapper;
    @Mock CommentMapper commentMapper;

    @InjectMocks ArticleService articleService;

    @Test
    void listArticles_shouldUseAscendingSortWhenRequested() {
        Article article = Article.builder().id(1).title("T").description("D").build();
        ArticleLightDto dto = new ArticleLightDto(1, "T", "D", LocalDateTime.now(), new UserLightDto(1L, "Alice"), new ThemeLightDto(1, "Java"));
        when(articleRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"))).thenReturn(List.of(article));
        when(articleMapper.toLightDto(article)).thenReturn(dto);

        List<ArticleLightDto> result = articleService.listArticles("asc");

        assertThat(result).containsExactly(dto);
    }

    @Test
    void listArticles_shouldDefaultToDescendingSort() {
        when(articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))).thenReturn(List.of());

        List<ArticleLightDto> result = articleService.listArticles("unexpected");

        assertThat(result).isEmpty();
    }

    @Test
    void getArticle_shouldReturnArticleWithMappedComments() {
        Article article = Article.builder().id(5).title("Title").description("Desc").build();
        Comment comment = Comment.builder().id(3).description("Comment").build();
        CommentDto commentDto = new CommentDto(3, "Comment", null, null, new UserLightDto(1L, "Alice"));
        ArticleDto articleDto = new ArticleDto(5, "Title", "Desc", null, null, null, null, List.of(commentDto));

        when(articleRepository.findById(5)).thenReturn(Optional.of(article));
        when(commentRepository.findAllByArticleIdOrderByCreatedAtAsc(5)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(articleMapper.toDto(article, List.of(commentDto))).thenReturn(articleDto);

        ArticleDto result = articleService.getArticle(5);

        assertThat(result).isEqualTo(articleDto);
    }

    @Test
    void getArticle_shouldThrowWhenArticleNotFound() {
        when(articleRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.getArticle(99))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createArticle_shouldPersistAndReturnMappedDto() {
        User user = User.builder().id(1L).email("a@b.com").name("Alice").password("pwd").build();
        Theme theme = Theme.builder().id(2).title("Java").description("desc").build();
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("New article");
        request.setDescription("Description");
        request.setThemeId(2);
        Article saved = Article.builder().id(10).title("New article").description("Description").user(user).theme(theme).build();
        ArticleDto dto = new ArticleDto(10, "New article", "Description", null, null, new UserLightDto(1L, "Alice"), new ThemeLightDto(2, "Java"), List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(themeRepository.findById(2)).thenReturn(Optional.of(theme));
        when(articleRepository.save(any(Article.class))).thenReturn(saved);
        when(articleMapper.toDto(saved, List.of())).thenReturn(dto);

        ArticleDto result = articleService.createArticle(1L, request);

        assertThat(result).isEqualTo(dto);
        verify(articleRepository).save(argThat(a ->
                a.getTitle().equals("New article") && a.getDescription().equals("Description") && a.getUser().equals(user) && a.getTheme().equals(theme)));
    }

    @Test
    void addComment_shouldPersistAndReturnMappedDto() {
        User user = User.builder().id(1L).email("a@b.com").name("Alice").password("pwd").build();
        Article article = Article.builder().id(10).title("Title").description("Desc").build();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setDescription("Great post");
        Comment saved = Comment.builder().id(4).description("Great post").user(user).article(article).build();
        CommentDto dto = new CommentDto(4, "Great post", null, null, new UserLightDto(1L, "Alice"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(articleRepository.findById(10)).thenReturn(Optional.of(article));
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(dto);

        CommentDto result = articleService.addComment(1L, 10, request);

        assertThat(result).isEqualTo(dto);
        verify(commentRepository).save(argThat(c ->
                c.getDescription().equals("Great post") && c.getUser().equals(user) && c.getArticle().equals(article)));
    }
}

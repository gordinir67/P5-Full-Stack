package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.models.Comment;
import com.openclassrooms.mddapi.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper(new UserMapper());

    @Test
    void toDto_shouldMapComment() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        Comment comment = new Comment();
        comment.setId(4);
        comment.setDescription("Nice article");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUser(user);

        var result = mapper.toDto(comment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getDescription()).isEqualTo("Nice article");
        assertThat(result.getUser().getName()).isEqualTo("Alice");
    }

    @Test
    void toDto_shouldReturnNullWhenCommentIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }
}
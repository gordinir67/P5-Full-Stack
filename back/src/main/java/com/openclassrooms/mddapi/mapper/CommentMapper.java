package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.comment.CommentDto;
import com.openclassrooms.mddapi.models.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        return new CommentDto(
                comment.getId(),
                comment.getDescription(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                userMapper.toLight(comment.getUser())
        );
    }
}
package com.openclassrooms.mddapi.dto.comment;

import com.openclassrooms.mddapi.dto.user.UserLightDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer id;

    @NotBlank
    @Size(max = 2000)
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserLightDto user;
}
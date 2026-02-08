package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.user.UserDto;
import com.openclassrooms.mddapi.dto.user.UserLightDto;
import com.openclassrooms.mddapi.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserLightDto toLight(User user) {
        if (user == null) return null;
        return new UserLightDto(user.getId(), user.getName());
    }
}
package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toDto_shouldMapUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("Alice");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        var result = mapper.toDto(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("user@example.com");
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void toDto_shouldReturnNullWhenUserIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toLight_shouldMapUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        var result = mapper.toLight(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void toLight_shouldReturnNullWhenUserIsNull() {
        assertThat(mapper.toLight(null)).isNull();
    }
}
package com.openclassrooms.mddapi.dto.theme;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeLightDto {
    private Integer id;

    @NotBlank
    private String title;
}
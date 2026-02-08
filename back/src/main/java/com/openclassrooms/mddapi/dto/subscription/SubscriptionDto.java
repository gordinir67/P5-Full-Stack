package com.openclassrooms.mddapi.dto.subscription;

import com.openclassrooms.mddapi.dto.theme.ThemeLightDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    private Integer id;
    private ThemeLightDto theme;
}
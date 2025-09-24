package org.example.ailifelegacy.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserTocResponseDto {
    private Long id;
    private String title;
    private Integer orderIndex;
}

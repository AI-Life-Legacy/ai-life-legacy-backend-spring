package org.example.ailifelegacy.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserQuestionResponseDto {
    private Long id;
    private String questionText;
    private Integer orderIndex;
}

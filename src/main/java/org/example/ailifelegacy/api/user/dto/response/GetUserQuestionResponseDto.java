package org.example.ailifelegacy.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserQuestionResponseDto {
    private Long id;
    private String questionText;
}

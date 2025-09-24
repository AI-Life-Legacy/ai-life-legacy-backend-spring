package org.example.ailifelegacy.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserAnswerResponseDto {
    private Long questionId;
    private String answerText;
}

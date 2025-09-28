package org.example.ailifelegacy.api.user.dto.response;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserTocWithQuestionsResponseDto {
    private Long tocId;
    private String tocTitle;
    private Integer orderIndex;
    private List<GetUserQuestionResponseDto> questions;
}
package org.example.ailifelegacy.api.life_legacy.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SaveUserAnswerDto {
    @NotBlank(message = "유저 최종 답변은 필수입니다.")
    private String answerText;
}

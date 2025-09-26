package org.example.ailifelegacy.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserAnswerDto {
    @NotBlank(message = "유저의 답변은 필수입니다.")
    private String newAnswerText;
}
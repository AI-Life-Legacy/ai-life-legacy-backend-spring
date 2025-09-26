package org.example.ailifelegacy.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class  SaveUserIntroDto{
    @NotBlank(message = "유저 자기소개는 필수입니다.")
    private String userIntro;
}

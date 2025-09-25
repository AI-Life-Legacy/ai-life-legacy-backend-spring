package org.example.ailifelegacy.api.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshTokenDto {
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}

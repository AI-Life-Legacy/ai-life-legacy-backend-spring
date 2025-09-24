package org.example.ailifelegacy.api.life_legacy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserAnswerDto {
    private String newAnswerText;
}
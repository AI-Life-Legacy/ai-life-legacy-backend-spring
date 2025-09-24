package org.example.ailifelegacy.api.life_legacy;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.life_legacy.dto.request.SaveUserAnswerDto;
import org.example.ailifelegacy.api.life_legacy.dto.response.GetUserQuestionsDto;
import org.example.ailifelegacy.common.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/life-legacy")
public class LifeLegacyController {
    private final LifeLegacyService lifeLegacyService;

    @GetMapping("/toc/{tocId}/questions")
    public ResponseEntity<SuccessResponse<List<GetUserQuestionsDto>>> getQuestions(Authentication authentication, @PathVariable("tocId") Long tocId) {
        UUID uuid = (UUID) authentication.getPrincipal();
        List<GetUserQuestionsDto> result = lifeLegacyService.getQuestions(tocId, uuid);

        return ResponseEntity
            .status(200)
            .body(SuccessResponse.of(result));
    }

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<SuccessResponse<Void>> answerQuestion(
        Authentication authentication, @PathVariable("questionid") Long questionId,
        @RequestBody SaveUserAnswerDto saveUserAnswerDto
    ) {
        UUID uuid = (UUID) authentication.getPrincipal();
        lifeLegacyService.saveAnswer(uuid, questionId, saveUserAnswerDto);
        return ResponseEntity
            .status(200)
            .body(SuccessResponse.created());
    }
}

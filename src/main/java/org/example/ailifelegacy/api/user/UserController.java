package org.example.ailifelegacy.api.user;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.user.dto.request.UpdateUserAnswerDto;
import org.example.ailifelegacy.api.user.dto.request.SaveUserIntroDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserAnswerResponseDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserTocResponseDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserTocWithQuestionsResponseDto;
import org.example.ailifelegacy.common.response.SuccessResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me")
public class UserController {
    private final UserService userService;

    @PostMapping("/intro")
    public ResponseEntity<SuccessResponse<Void>> saveUserIntro(Authentication authentication, @RequestBody
        SaveUserIntroDto saveUserIntroDto) {
        UUID uuid = (UUID) authentication.getPrincipal();
        userService.saveUserIntro(uuid, saveUserIntroDto);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.created());
    }

    @GetMapping("/toc")
    public ResponseEntity<SuccessResponse<List<GetUserTocResponseDto>>> getUserToc(Authentication authentication) {
        UUID uuid = (UUID) authentication.getPrincipal();
        List<GetUserTocResponseDto> result = userService.getUserToc(uuid);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(result));
    }

    @GetMapping("/toc-questions")
    public ResponseEntity<SuccessResponse<List<GetUserTocWithQuestionsResponseDto>>> getUserTocQuestions(Authentication authentication) {
        UUID uuid = (UUID) authentication.getPrincipal();
        List<GetUserTocWithQuestionsResponseDto> result = userService.getUserTocAndQuestions(uuid);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(result));
    }


    @GetMapping("/answers")
    public ResponseEntity<SuccessResponse<GetUserAnswerResponseDto>> getUserAnswer(Authentication authentication, @Param("questionId") Long questionId) {
        UUID uuid = (UUID) authentication.getPrincipal();
        GetUserAnswerResponseDto result = userService.getUserAnswer(questionId, uuid);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(result));
    }

    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<SuccessResponse<Void>> updateUserAnswer(Authentication authentication, @PathVariable("answerId") Long answerId, @RequestBody
        UpdateUserAnswerDto updateUserAnswerDto) {
        UUID uuid = (UUID) authentication.getPrincipal();
        userService.updateUserAnswer(answerId, uuid, updateUserAnswerDto);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.updated());
    }
}

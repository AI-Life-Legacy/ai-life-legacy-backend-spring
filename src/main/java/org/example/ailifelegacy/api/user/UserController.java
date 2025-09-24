package org.example.ailifelegacy.api.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.user.dto.request.SaveUserIntroDto;
import org.example.ailifelegacy.common.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
}

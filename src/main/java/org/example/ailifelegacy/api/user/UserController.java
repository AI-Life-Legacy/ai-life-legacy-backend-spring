package org.example.ailifelegacy.api.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.common.response.SuccessResponse;
import org.example.ailifelegacy.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<SuccessResponse<User>> getUser(Authentication authentication) {
        UUID uuid = (UUID) authentication.getPrincipal();
        User user = userService.findByUuid(uuid);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(user));
    }
}

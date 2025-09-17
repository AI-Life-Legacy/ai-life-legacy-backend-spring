package org.example.ailifelegacy.api.auth;

import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.auth.dto.LoginDto;
import org.example.ailifelegacy.api.auth.dto.SignupDto;
import org.example.ailifelegacy.common.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<String>> login(@RequestBody LoginDto loginDto) {
        String accessToken = authService.login(loginDto);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(accessToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<String>> signup(@RequestBody SignupDto signupDto) {
        String accessToken = authService.signup(signupDto);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.created(accessToken));
    }
}

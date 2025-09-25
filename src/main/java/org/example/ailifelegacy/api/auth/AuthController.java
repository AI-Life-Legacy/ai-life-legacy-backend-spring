package org.example.ailifelegacy.api.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.auth.dto.request.LoginDto;
import org.example.ailifelegacy.api.auth.dto.request.RefreshTokenDto;
import org.example.ailifelegacy.api.auth.dto.request.SignupDto;
import org.example.ailifelegacy.api.auth.dto.response.LoginResponseDto;
import org.example.ailifelegacy.api.auth.dto.response.RefreshTokenResponseDto;
import org.example.ailifelegacy.api.auth.dto.response.SignupResponseDto;
import org.example.ailifelegacy.common.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponseDto>> login(
        @Valid @RequestBody LoginDto loginDto
    ) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(loginResponseDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<SignupResponseDto>> signup(
        @Valid @RequestBody SignupDto signupDto
    ) {
        SignupResponseDto signupResponseDto = authService.signup(signupDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SuccessResponse.created(signupResponseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<RefreshTokenResponseDto>> refreshToken(
        @Valid @RequestBody RefreshTokenDto refreshTokenDto
    ) {
        RefreshTokenResponseDto refreshTokenResponseDto = authService.refreshToken(refreshTokenDto);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(refreshTokenResponseDto));
    }
}

package org.example.ailifelegacy.api.auth;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.auth.dto.request.LoginDto;
import org.example.ailifelegacy.api.auth.dto.request.RefreshTokenDto;
import org.example.ailifelegacy.api.auth.dto.request.SignupDto;
import org.example.ailifelegacy.api.auth.dto.response.LoginResponseDto;
import org.example.ailifelegacy.api.auth.dto.response.RefreshTokenResponseDto;
import org.example.ailifelegacy.api.auth.dto.response.SignupResponseDto;
import org.example.ailifelegacy.api.auth_identity.AuthIdentityRepository;
import org.example.ailifelegacy.api.auth_identity.entity.AuthIdentity;
import org.example.ailifelegacy.api.jwt.JwtProperties;
import org.example.ailifelegacy.api.jwt.TokenProvider;
import org.example.ailifelegacy.api.jwt.dto.TokenPair;
import org.example.ailifelegacy.api.refresh_token.RefreshTokenRepository;
import org.example.ailifelegacy.api.refresh_token.entity.RefreshToken;
import org.example.ailifelegacy.api.user.UserRepository;
import org.example.ailifelegacy.api.user.entity.User;
import org.example.ailifelegacy.common.enums.AuthIdentityEnums.Provider;
import org.example.ailifelegacy.common.error.exception.ConflictException;
import org.example.ailifelegacy.common.error.exception.NotFoundException;
import org.example.ailifelegacy.common.error.exception.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponseDto login(LoginDto loginDto) {
        AuthIdentity userAuthIdentity = authIdentityRepository.findByProviderUuid(loginDto.getEmail())
            .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), userAuthIdentity.getPassword())) throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");

        User user = userAuthIdentity.getUser();
        TokenPair tokenPair = tokenProvider.generateTokenPair(user);

        saveOrUpdateRefreshToken(user, tokenPair.getRefreshToken());

        return new LoginResponseDto(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
    }

    @Transactional
    public SignupResponseDto signup(SignupDto signupDto) {
        try {
            User user = User.createEmptyUser();
            userRepository.save(user);

            AuthIdentity authIdentity = AuthIdentity.builder()
                .providerUuid(signupDto.getEmail())
                .provider(Provider.LOCAL)
                .password(passwordEncoder.encode(signupDto.getPassword()))
                .user(user)
                .build();
            authIdentityRepository.save(authIdentity);

            // 5. 새 토큰 발급 및 저장
            TokenPair tokenPair = tokenProvider.generateTokenPair(user);

            saveOrUpdateRefreshToken(user, tokenPair.getRefreshToken());

            return new SignupResponseDto(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }
    }

    @Transactional
    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String clientRefreshToken = refreshTokenDto.getRefreshToken();

        // 1. refresh token 검증
        if (!tokenProvider.validRefreshToken(clientRefreshToken)) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. refresh token에 저장되어 있는 uuid로 사용자 조회
        Authentication auth = tokenProvider.getRefreshTokenAuthentication(clientRefreshToken);
        UUID userUuid = (UUID) auth.getPrincipal();
        User user = userRepository.findById(userUuid)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 3. DB에 저장된 refresh token 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("리프레시 토큰이 존재하지 않습니다."));

        // 4. 값 검증
        PasswordEncoder tokenEncoder = new MessageDigestPasswordEncoder("SHA-256");
        if (!tokenEncoder.matches(clientRefreshToken, tokenEntity.getTokenHash())) {
            throw new UnauthorizedException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 5. 새 토큰 발급 및 저장
        TokenPair tokenPair = tokenProvider.generateTokenPair(user);

        saveOrUpdateRefreshToken(user, tokenPair.getRefreshToken());

        return new RefreshTokenResponseDto(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
    }

    // Refresh Token 저장 또는 갱신
    private void saveOrUpdateRefreshToken(User user, String refreshToken) {
        PasswordEncoder tokenEncoder = new MessageDigestPasswordEncoder("SHA-256");
        String hashedRefreshToken = tokenEncoder.encode(refreshToken);
        LocalDateTime expiresAt = tokenProvider.calculateExpiry(jwtProperties.getRefreshTokenExpiration());

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByUser(user);
        if (tokenOpt.isPresent()) {
            // 리프레시 토큰이 존재하는 경우 Update하기
            RefreshToken tokenEntity = tokenOpt.get();
            tokenEntity.updateToken(hashedRefreshToken, expiresAt);
        } else {
            // 리프레시 토큰이 DB에 없는 경우 생성 후 저장
            RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashedRefreshToken)
                .expiresAt(expiresAt)
                .build();
            refreshTokenRepository.save(newToken);
        }
    }
}
package org.example.ailifelegacy.api.auth;

import jakarta.transaction.Transactional;
import java.time.Duration;
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
import org.example.ailifelegacy.api.refresh_token.RefreshTokenRepository;
import org.example.ailifelegacy.api.refresh_token.entity.RefreshToken;
import org.example.ailifelegacy.api.user.UserRepository;
import org.example.ailifelegacy.api.user.entity.User;
import org.example.ailifelegacy.common.enums.AuthIdentityEnums.Provider;
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

    public LoginResponseDto login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        // 이메일로 사용자 찾기
        AuthIdentity userAuthIdentity = authIdentityRepository.findByProviderUuid((email));
        if (userAuthIdentity == null) throw new IllegalArgumentException("존재하지 않는 사용자입니다.");


        // 비밀번호 확인
        if (!passwordEncoder.matches(password, userAuthIdentity.getPassword())) throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        User user = userAuthIdentity.getUser();
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        // 만료일자 계산
        Date now = new Date();
        Date expiresAtDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());
        LocalDateTime expiresAt = expiresAtDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        // SHA-256 해시
        PasswordEncoder tokenEncoder = new MessageDigestPasswordEncoder("SHA-256");
        String hashedRefreshToken = tokenEncoder.encode(refreshToken);

        // DB에 저장되어 있는 리프레시 토큰 업데이트 -> 없으면 생성
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByUser(user);
        if (tokenOpt.isPresent()) {
            RefreshToken tokenEntity = tokenOpt.get();
            tokenEntity.updateToken(hashedRefreshToken, expiresAt);
            refreshTokenRepository.save(tokenEntity);
        } else {
            RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashedRefreshToken)
                .expiresAt(expiresAt)
                .build();
            refreshTokenRepository.save(newToken);
        }

        return LoginResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Transactional
    public SignupResponseDto signup(SignupDto signupDto) {
        // 이메일 중복 체크
        String email = signupDto.getEmail();
        String password = signupDto.getPassword();

        try {
            // 유저 생성
            User user = User.createEmptyUser();
            userRepository.save(user);

            // 인증 정보 생성
            AuthIdentity authIdentity = AuthIdentity.builder()
                .providerUuid(email)
                .provider(Provider.LOCAL)
                .password(passwordEncoder.encode(password))
                .user(user)
                .build();
            authIdentityRepository.save(authIdentity);

            // 토큰 발급
            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            // 여기서 만료일자 추출
            Date now = new Date();
            Date expiresAtDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

            // Date → LocalDateTime 변환
            LocalDateTime expiresAt = expiresAtDate.toInstant()
                .atZone(ZoneId.systemDefault()) // 시스템 기본 타임존 기준
                .toLocalDateTime();

            // SHA-256 해시
            PasswordEncoder tokenEncoder = new MessageDigestPasswordEncoder("SHA-256");
            String hashedRefreshToken = tokenEncoder.encode(refreshToken);

            RefreshToken saveRefreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashedRefreshToken) // 해시 저장
                .expiresAt(expiresAt) // LocalDateTime 타입으로 저장
                .build();
            refreshTokenRepository.save(saveRefreshToken);

            return SignupResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.", e);
        }
    }

    @Transactional
    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String clientRefreshToken = refreshTokenDto.getRefreshToken();

        // 1. refresh token 검증 (JWT 서명 및 만료 확인)
        if (!tokenProvider.validRefreshToken(clientRefreshToken)) throw new IllegalArgumentException("유효하지 않은 토큰입니다.");

        // 2. 사용자 조회
        Authentication getUserAuthentication = tokenProvider.getRefreshTokenAuthentication(clientRefreshToken);
        UUID userUuid = (UUID) getUserAuthentication.getPrincipal();
        User user = userRepository.findByUuid(userUuid)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. DB에 저장된 refresh token 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰이 존재하지 않습니다."));

        // 4. 값이 일치하는지 확인 (해시 매칭)
        PasswordEncoder tokenEncoder = new MessageDigestPasswordEncoder("SHA-256");
        if (!tokenEncoder.matches(clientRefreshToken, tokenEntity.getTokenHash())) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 5. 새 access/refresh token 발급
        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        Date now = new Date();
        Date expiresAtDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());
        LocalDateTime newExpiresAt = expiresAtDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        // 6. DB에 refresh token 갱신
        String hashedRefreshToken = tokenEncoder.encode(newRefreshToken);
        tokenEntity.updateToken(hashedRefreshToken, newExpiresAt);
        refreshTokenRepository.save(tokenEntity);

        // 7. 새 토큰 반환
        return RefreshTokenResponseDto.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .build();
    }
}

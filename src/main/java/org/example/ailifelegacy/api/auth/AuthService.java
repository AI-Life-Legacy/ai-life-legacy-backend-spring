package org.example.ailifelegacy.api.auth;

import jakarta.transaction.Transactional;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.auth.dto.LoginDto;
import org.example.ailifelegacy.api.auth.dto.SignupDto;
import org.example.ailifelegacy.entity.User;
import org.example.ailifelegacy.api.jwt.TokenProvider;
import org.example.ailifelegacy.api.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("User not found with email: " + email);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }

    @Transactional
    public String signup(SignupDto signupDto) {
        // 이메일 중복 체크
        String email = signupDto.getEmail();
        String password = signupDto.getPassword();

        boolean existUser = userRepository.findByEmail(email).isPresent();
        if (existUser)
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");

        // 유저 생성 (비밀번호는 반드시 암호화)
        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .build();

        try {
            User savedUser = userRepository.save(user);
            return tokenProvider.generateToken(savedUser, Duration.ofHours(2));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.", e);
        }
    }
}

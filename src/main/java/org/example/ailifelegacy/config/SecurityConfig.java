// package/org/example/ailifelegacy/config/SecurityConfig.java
package org.example.ailifelegacy.config;

import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.common.exception.JwtAccessDeniedHandler;
import org.example.ailifelegacy.common.exception.JwtAuthenticationEntryPoint;
import org.example.ailifelegacy.api.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable) // CSRF 공격 방지 기능 (웹폼 기반) → REST API는 불필요하므로 꺼줌
            .cors(Customizer.withDefaults()) // CORS 기본 설정 활성화 (프론트엔드-백엔드 다른 도메인 허용)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 세션을 만들지 않음 (JWT 방식 → 요청마다 토큰 인증)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/login", "/signup").permitAll() // 로그인/회원가입은 토큰 없이 접근 가능
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401 처리
                .accessDeniedHandler(jwtAccessDeniedHandler)           // 403 처리
            )
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // 우리 JWT 필터를 스프링 기본 로그인 필터 앞에 추가
            .formLogin(AbstractHttpConfigurer::disable) // 스프링 내장 폼 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable) // 기본 HTTP Basic 인증 비활성화
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package org.example.ailifelegacy.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. Authorization 헤더 추
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        // 2. 토큰 부분만 잘라내기 ("Bearer " 제거)
        String token = getAccessToken(authorizationHeader);

        // 3. 토큰 유효성 검증
        if (token != null && tokenProvider.validToken(token)) {
            // 4. 토큰에서 Authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(token);
            // 5. SecurityContextHolder에 저장
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        // 6. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}


package org.example.ailifelegacy.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + jwtProperties.getAccessTokenExpiration()), user, jwtProperties.getAccessTokenSecretKey());
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration()), user, jwtProperties.getRefreshTokenSecretKey());
    }

    private String makeToken(Date expiry, User user, String secretKey) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getUuid().toString())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validAccessToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getAccessTokenSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validRefreshToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getRefreshTokenSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAccessTokenAuthentication(String token) {
        Claims claims = getClaims(token, jwtProperties.getAccessTokenSecretKey());
        String uuidString = claims.getSubject();
        UUID uuid = UUID.fromString(uuidString);

        // role은 지금 안 쓰니까 빈 리스트
        return new UsernamePasswordAuthenticationToken(uuid, null, Collections.emptyList());
    }

    public Authentication getRefreshTokenAuthentication(String token) {
        Claims claims = getClaims(token, jwtProperties.getRefreshTokenSecretKey());
        String uuidString = claims.getSubject();
        UUID uuid = UUID.fromString(uuidString);

        // role은 지금 안 쓰니까 빈 리스트
        return new UsernamePasswordAuthenticationToken(uuid, null, Collections.emptyList());
    }

    private Claims getClaims(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}

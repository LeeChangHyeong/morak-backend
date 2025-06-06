package org.brokong.morakbackend.global;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long accessTokenExpireTime = 1000 * 60 * 60; // 토큰 만료 1시간

    // AccessToken 생성
    public String createAccessToken(String userEmail, String userRole) {

        return Jwts.builder()
                .setSubject(userEmail)
                .claim("role", userRole)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // AccessToken 에서 이메일 추출
    public String getEmailFromAccessToken(String accessToken) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    // AccessToken 에서 role 추출
    public String getRoleFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(accessToken);

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");

            return false;
        } catch (JwtException e) {
            log.warn("JWT 토큰이 유효하지 않습니다.");

            return false;
        }
    }
}
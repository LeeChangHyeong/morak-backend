package org.brokong.morakbackend.global.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.user.enums.UserRoles;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	private final long accessTokenExpireTime = 1000 * 60 * 10; // 토큰 만료 10분
	private final long refreshTokenExpireTime = 1000 * 60 * 60 * 24 * 14; // 리프레시 토큰 만료 14일

	// AccessToken 생성
	public String createAccessToken(String userEmail, String userRole, String nickname, Long userId) {

		return Jwts.builder()
				   .setSubject(userEmail)
				   .claim("userId", userId)
				   .claim("role", userRole)
				   .claim("nickname", nickname)
				   .setIssuedAt(new Date())
				   .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireTime))
				   .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
				   .compact();
	}

	// AccessToken 에서 userId 추출
	public Long getUserIdFromAccessToken(String token) {
		return Jwts.parserBuilder()
				   .setSigningKey(secretKey.getBytes())
				   .build()
				   .parseClaimsJws(token)
				   .getBody()
				   .get("userId", Long.class);
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

	// AccessToken 에서 nickname 추출
	public String getNicknameFromAccessToken(String token) {
		return Jwts.parserBuilder()
				   .setSigningKey(secretKey.getBytes())
				   .build()
				   .parseClaimsJws(token)
				   .getBody()
				   .get("nickname", String.class);
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
			throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "만료된 JWT 토큰입니다.");
		} catch (JwtException e) {
			log.warn("JWT 토큰이 유효하지 않습니다.");
			throw new JwtException("유효하지 않은 JWT 토큰입니다.");
		}
	}

	// 헤더에서 AccessToken 가져오기
	public String extractAccessToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}

		return null;
	}

	/**
	 * JWT 토큰으로부터 UserPrincipal 생성 (WebSocket용)
	 */
	// UserPrincipal 생성 메서드
	public UserPrincipal createUserPrincipalFromToken(String token) {
		try {
			String email = getEmailFromAccessToken(token);
			String roleStr = getRoleFromAccessToken(token);
			Long userId = getUserIdFromAccessToken(token);
			String nickname = getNicknameFromAccessToken(token);

			return UserPrincipal.builder()
								.id(userId)
								.email(email)
								.nickname(nickname)
								.role(UserRoles.valueOf(roleStr))
								.status(UserStatus.ACTIVE) // 기본값
								.build();

		} catch (Exception e) {
			throw new JwtException("토큰에서 사용자 정보 추출 실패: " + e.getMessage());
		}
	}

	// 토큰 남은 시간 계산
	public long getAccessTokenExpireTime(String accessToken) {
		Date expiration = Jwts.parserBuilder()
							  .setSigningKey(secretKey.getBytes())
							  .build()
							  .parseClaimsJws(accessToken)
							  .getBody()
							  .getExpiration();

		return expiration.getTime() - System.currentTimeMillis();
	}

	// ========== RefreshToken 관련 메서드들 ==========

	// RefreshToken 생성
	public String createRefreshToken(String userEmail, Long userId) {
		return Jwts.builder()
				   .setSubject(userEmail)
				   .claim("userId", userId)
				   .claim("type", "refresh")
				   .setIssuedAt(new Date())
				   .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
				   .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
				   .compact();
	}

	// RefreshToken에서 이메일 추출
	public String getEmailFromRefreshToken(String refreshToken) {
		return Jwts.parserBuilder()
				   .setSigningKey(secretKey.getBytes())
				   .build()
				   .parseClaimsJws(refreshToken)
				   .getBody()
				   .getSubject();
	}

	// RefreshToken에서 userId 추출
	public Long getUserIdFromRefreshToken(String refreshToken) {
		return Jwts.parserBuilder()
				   .setSigningKey(secretKey.getBytes())
				   .build()
				   .parseClaimsJws(refreshToken)
				   .getBody()
				   .get("userId", Long.class);
	}

	// RefreshToken 유효성 검증
	public boolean validateRefreshToken(String refreshToken) {
		try {
			var claims = Jwts.parserBuilder()
					.setSigningKey(secretKey.getBytes())
					.build()
					.parseClaimsJws(refreshToken)
					.getBody();

			return "refresh".equals(claims.get("type"));
		} catch (ExpiredJwtException e) {
			log.warn("만료된 RefreshToken입니다.");
			throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "만료된 RefreshToken입니다.");
		} catch (JwtException e) {
			log.warn("RefreshToken이 유효하지 않습니다.");
			throw new JwtException("유효하지 않은 RefreshToken입니다.");
		}
	}
}
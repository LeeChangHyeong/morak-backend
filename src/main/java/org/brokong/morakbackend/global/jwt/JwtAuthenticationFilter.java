package org.brokong.morakbackend.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brokong.morakbackend.global.Security.UserPrincipal;
import org.brokong.morakbackend.global.redis.RedisService;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String accessToken = jwtUtil.extractAccessToken(request);

            // 토큰이 있을 때만 검증 처리
            if (accessToken != null) {
                // 블랙리스트 여부 확인
                String isBlacklisted = redisService.getValue("access_token_blacklist:" + accessToken);
                if (isBlacklisted != null) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
                    return;
                }

                // 토큰 검증 (예외가 발생할 수 있음)
                jwtUtil.validateAccessToken(accessToken);

                // 정상 토큰이면 인증 정보 설정
                String email = jwtUtil.getEmailFromAccessToken(accessToken);

                User user = userRepository.findByEmail(email)
                                          .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

                if (user.getStatus() == UserStatus.BLOCKED) {
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "차단된 사용자입니다. 관리자에게 문의해주세요.");
                    return;
                }

                if (user.getStatus() == UserStatus.WITHDRAWN) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "탈퇴한 사용자입니다.");
                    return;
                }

                UserPrincipal userPrincipal = UserPrincipal.from(user);

                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("❗ 만료된 JWT 토큰: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다. 다시 로그인해주세요.");
        } catch (JwtException e) {
            log.warn("❗ 유효하지 않은 JWT 토큰: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("❗ JWT 필터에서 예외 발생: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        ResponseDto<Void> errorResponse = new ResponseDto<>(message, null);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}

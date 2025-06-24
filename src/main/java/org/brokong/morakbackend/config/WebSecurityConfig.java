package org.brokong.morakbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.jwt.JwtAuthenticationFilter;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	// 인증 실패 시 처리
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");

			ResponseDto<Void> errorResponse = new ResponseDto<>("로그인이 필요한 서비스입니다.", null);
			String jsonResponse = objectMapper.writeValueAsString(errorResponse);

			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		};
	}

	// 권한 부족 시 처리
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json;charset=UTF-8");

			ResponseDto<Void> errorResponse = new ResponseDto<>("해당 작업을 수행할 권한이 없습니다.", null);
			String jsonResponse = objectMapper.writeValueAsString(errorResponse);

			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		};
	}

	// 특정 경로 Security Off
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(csrf -> csrf.disable()) // JWT 방식에서는 CSRF 비활성화
			.authorizeHttpRequests(auth -> auth
				// ✅ 회원가입
				.requestMatchers(
					apiPrefix + "/auth/signup",
					apiPrefix + "/auth/login",
					apiPrefix + "/auth/check-nickname",
					apiPrefix + "/auth/check-email",
					apiPrefix + "/email/**"
				).permitAll()

				// ✅ GET 요청만 허용하는 공개 API
				.requestMatchers(HttpMethod.GET, apiPrefix + "/posts").permitAll()
				.requestMatchers(HttpMethod.GET, apiPrefix + "/posts/*").permitAll()
				.requestMatchers(HttpMethod.GET, apiPrefix + "/comments/root").permitAll()
				.requestMatchers(HttpMethod.GET, apiPrefix + "/comments/*/replies").permitAll()
				.requestMatchers(HttpMethod.GET, apiPrefix + "/users/search").permitAll()
				.requestMatchers(HttpMethod.GET, apiPrefix + "/users/*/nickname/*").permitAll()
				.requestMatchers(HttpMethod.GET, apiPrefix + "/users/*").permitAll()

				// ✅ 나머지 모든 요청은 인증 필요
				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(authenticationEntryPoint())
				.accessDeniedHandler(accessDeniedHandler())
			)
			.formLogin(form -> form.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
    }
}

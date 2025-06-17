package org.brokong.morakbackend.global.Security;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

	public static String getLoginEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("로그인 상태가 아닙니다.");
		}

		return (String) authentication.getPrincipal();
	}

	public static Optional<String> getOptionalLoginEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty(); // 로그인 안 된 경우
		}

		Object principal = authentication.getPrincipal();

		// "anonymousUser" 체크 (스프링 시큐리티의 기본 익명 사용자)
		if (principal instanceof String str && str.equals("anonymousUser")) {
			return Optional.empty();
		}

		return Optional.of((String) principal);
	}


}

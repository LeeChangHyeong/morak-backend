package org.brokong.morakbackend.global.Security;

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

}

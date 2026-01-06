package org.brokong.morakbackend.global.redis;

public final class RedisKey {

	private RedisKey() {}

	public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
	public static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "access_token_blacklist:";
	public static final String SESSION_ID_PREFIX = "sessionId:";
	public static final String USER_SESSIONS_PREFIX = "user_sessions:";

	public static String refreshTokenKey(String email) {
		return REFRESH_TOKEN_PREFIX + email;
	}

	public static String accessTokenBlacklistKey(String token) {
		return ACCESS_TOKEN_BLACKLIST_PREFIX + token;
	}

	public static String sessionKey(String sessionId) {
		return SESSION_ID_PREFIX + sessionId;
	}

	public static String userSessionsKey(Long userId) {
		return USER_SESSIONS_PREFIX + userId;
	}
}

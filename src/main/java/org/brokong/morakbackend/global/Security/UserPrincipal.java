package org.brokong.morakbackend.global.Security;

import lombok.Builder;
import lombok.Getter;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.enums.UserRoles;
import org.brokong.morakbackend.user.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Builder
public class UserPrincipal implements UserDetails {

	private Long id;
	private String email;
	private String nickname;
	private UserRoles role;
	private UserStatus status;

	public static UserPrincipal from(User user) {
		return UserPrincipal.builder()
							.id(user.getId())
							.email(user.getEmail())
							.nickname(user.getNickname())
							.role(user.getRole())
							.status(user.getStatus())
							.build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getPassword() {
		return null; // JWT 기반이므로 password는 사용하지 않음
	}

	@Override
	public String getUsername() {
		return nickname;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return status != UserStatus.BLOCKED;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status == UserStatus.ACTIVE;
	}
}
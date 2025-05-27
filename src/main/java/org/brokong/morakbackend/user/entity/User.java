package org.brokong.morakbackend.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import org.brokong.morakbackend.global.BaseEntity;
import org.brokong.morakbackend.user.enums.LoginType;
import org.brokong.morakbackend.user.enums.UserStatus;

@Entity(name = "users")
public class User extends BaseEntity {

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private LoginType loginType;

	@Column(nullable = false)
	private String pushToken;

	@Column(nullable = false)
	private UserStatus status;

}

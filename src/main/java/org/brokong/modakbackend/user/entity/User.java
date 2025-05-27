package org.brokong.modakbackend.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import org.brokong.modakbackend.global.BaseEntity;
import org.brokong.modakbackend.user.enums.LoginType;
import org.brokong.modakbackend.user.enums.Status;

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
	private Status status;

}

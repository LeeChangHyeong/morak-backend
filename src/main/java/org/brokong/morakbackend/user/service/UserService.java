package org.brokong.morakbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.global.request.ReportRequestDto;
import org.brokong.morakbackend.report.entity.UserReport;
import org.brokong.morakbackend.report.repository.UserReportRepository;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
	private final UserReportRepository userReportRepository;

	public UserResponseDto getMyInfo(UserPrincipal userPrincipal) {

		if (userPrincipal == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        return UserResponseDto.from(user);
    }


    public UserResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserResponseDto.from(user);
    }

    public UserResponseDto getUserByNickname(String nickname) {

        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserResponseDto.from(user);
    }


    public List<UserResponseDto> searchUsersByNickname(String nickname) {

        List<User> users = userRepository.findByNicknameContaining(nickname);

        return users.stream()
                .map(UserResponseDto::from)
                .toList();
    }

	public List<UserResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
            .map(UserResponseDto::from)
            .toList();
	}

	public void blockUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.block();
        userRepository.save(user);
	}

	public void withdrawal(UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        user.withdraw();

        userRepository.save(user);
    }

	@Transactional
	public void reportUser(Long userId, ReportRequestDto requestDto, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		User targetUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if(userReportRepository.existsByUserAndTargetUser(user, targetUser)) {
			throw new IllegalArgumentException("이미 신고한 사용자입니다.");
		}

		UserReport userReport = new UserReport(user, targetUser, requestDto.getReason());
		userReportRepository.save(userReport);
	}
}

package org.brokong.morakbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.user.dto.response.UserResponseDto;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getMyInfo() {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (email == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        return new UserResponseDto(user);
    }


    public UserResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return new UserResponseDto(user);
    }

    public UserResponseDto getUserByNickname(String nickname) {

        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return new UserResponseDto(user);
    }


    public List<UserResponseDto> searchUsersByNickname(String nickname) {

        List<User> users = userRepository.findByNicknameContaining(nickname);

        return users.stream()
                .map(UserResponseDto::new)
                .toList();
    }
}

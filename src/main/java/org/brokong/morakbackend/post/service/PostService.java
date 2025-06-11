// package org.brokong.morakbackend.post.service;
//
// import lombok.RequiredArgsConstructor;
// import org.brokong.morakbackend.post.dto.PostResponseDto;
// import org.brokong.morakbackend.post.entity.Post;
// import org.brokong.morakbackend.user.entity.User;
// import org.brokong.morakbackend.user.repository.UserRepository;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;
//
// @Service
// @RequiredArgsConstructor
// public class PostService {
//
// 	private final UserRepository userRepository;
//
// 	public PostResponseDto createPost(String content) {
//
// 		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
// 		User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
//
// 		Post post = Post.builder()
// 			.user(user)
// 			.content(content)
// 			.build();
//
//
// 		return new PostResponseDto(post);
// 	}
// }

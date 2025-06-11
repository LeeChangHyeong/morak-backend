package org.brokong.morakbackend.post.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.post.dto.PostResponseDto;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.repository.PostRepository;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostResponseDto createPost(String content) {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Post post = Post.builder()
                .user(user)
                .content(content)
                .build();

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    public void deletePost(Long postId) {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.deleteById(postId);
    }
}

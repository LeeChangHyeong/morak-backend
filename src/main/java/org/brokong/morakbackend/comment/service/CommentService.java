package org.brokong.morakbackend.comment.service;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.comment.dto.CommentRequestDto;
import org.brokong.morakbackend.comment.dto.CommentResponseDto;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.comment.repository.CommentRepository;
import org.brokong.morakbackend.global.Security.SecurityUtil;
import org.brokong.morakbackend.like.Repository.CommentLikeRepository;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.repository.PostRepository;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository	commentRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentLikeRepository commentLikeRepository;

	public CommentResponseDto createComment(CommentRequestDto request) {

		String email = SecurityUtil.getLoginEmail();
		User user = userRepository.findByEmail(email).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);
		Post post = postRepository.findById(request.getPostId()).orElseThrow(
			() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다.")
			);

		Comment parentComment = null;

		if (request.getParentId() != null) {
			parentComment = commentRepository.findById(request.getParentId())
											 .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

			if (parentComment != null && parentComment.getParentComment() != null) {
				throw new IllegalArgumentException("대댓글의 대댓글은 허용되지 않습니다.");
			}
		}


		Comment comment = Comment.builder()
								 .post(post)
								 .user(user)
								 .parentComment(parentComment)  // null 또는 부모 댓글 객체
								 .content(request.getContent())
								 .build();

		commentRepository.save(comment);

		boolean likedByLoginUser = commentLikeRepository.existsByCommentAndUser(comment, user);

		return CommentResponseDto.from(comment, likedByLoginUser);
	}

	public void deleteComment(Long commentId) {
		String email = SecurityUtil.getLoginEmail();



	}
}

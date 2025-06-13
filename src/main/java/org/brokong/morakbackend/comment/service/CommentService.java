package org.brokong.morakbackend.comment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentLikeRepository commentLikeRepository;

	@Transactional
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

	@Transactional
	public void deleteComment(Long commentId) {
		String email = SecurityUtil.getLoginEmail();

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
		);

		if (!comment.getUser().getEmail().equals(email)) {
			throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
		}

		comment.delete();

		commentRepository.save(comment);
	}

	public List<CommentResponseDto> getCommentsByPostId(Long postId) {

		String email = SecurityUtil.getLoginEmail();

		User user = userRepository.findByEmail(email).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);

		Post post = postRepository.findById(postId).orElseThrow(
			() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다.")
		);

		List<Comment> comments = commentRepository.findAllByPostWithUserAndParent(post);

		// 댓글을 DTO로 변환
		Map<Long, CommentResponseDto> dtoMap = new HashMap<>();
		for (Comment comment : comments) {
			boolean likedByLoginUser = commentLikeRepository.existsByCommentAndUser(comment, user);
			dtoMap.put(comment.getId(), CommentResponseDto.from(comment, likedByLoginUser));
		}

		// 댓글 - 대댓글 관계 구성
		List<CommentResponseDto> result = new ArrayList<>();
		for (Comment comment : comments) {
			Long parentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;

			if (parentId == null) {
				result.add(dtoMap.get(comment.getId()));
			} else {
				CommentResponseDto parentDto = dtoMap.get(parentId);
				if (parentDto != null) {
					parentDto.getChildren().add(dtoMap.get(comment.getId()));
				}
			}
		}

		return result;
	}
}
package org.brokong.morakbackend.comment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.comment.dto.CommentRequestDto;
import org.brokong.morakbackend.comment.dto.CommentResponseDto;
import org.brokong.morakbackend.comment.dto.CommentUpdateRequestDto;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.comment.repository.CommentRepository;
import org.brokong.morakbackend.global.Security.SecurityUtil;
import org.brokong.morakbackend.like.Repository.CommentLikeRepository;
import org.brokong.morakbackend.like.entity.CommentLike;
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

		if(comment.isDeleted()) {
			throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
		}

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

		// 댓글 + 작성자 + 부모 댓글까지 모두 fetch join으로 가져오기
		List<Comment> comments = commentRepository.findAllByPostWithUserAndParent(post);

		// 댓글 ID 목록 추출
		List<Long> commentIds = comments.stream()
										.map(Comment::getId)
										.collect(Collectors.toList());

		// 로그인 유저가 좋아요 누른 댓글 ID 목록 조회 (쿼리 1회)
		List<CommentLike> likes = commentLikeRepository.findAllByCommentIdInAndUser(commentIds, user);
		Set<Long> likedCommentIds = likes.stream()
										 .map(like -> like.getComment().getId())
										 .collect(Collectors.toSet());

		// 댓글을 DTO로 변환
		Map<Long, CommentResponseDto> dtoMap = new HashMap<>();
		for (Comment comment : comments) {
			boolean likedByLoginUser = likedCommentIds.contains(comment.getId());
			dtoMap.put(comment.getId(), CommentResponseDto.from(comment, likedByLoginUser));
		}

		// 댓글 - 대댓글 계층 구성
		List<CommentResponseDto> result = new ArrayList<>();
		for (Comment comment : comments) {
			Long parentId = comment.getParentComment() != null
							? comment.getParentComment().getId()
							: null;

			if (parentId == null) {
				result.add(dtoMap.get(comment.getId())); // 최상위 댓글
			} else {
				CommentResponseDto parentDto = dtoMap.get(parentId);
				if (parentDto != null) {
					parentDto.getChildren().add(dtoMap.get(comment.getId())); // 대댓글 추가
				}
			}
		}

		return result;
	}


	@Transactional
	public CommentResponseDto updateComment(Long commentId, CommentUpdateRequestDto request) {

		String email = SecurityUtil.getLoginEmail();

		User user = userRepository.findByEmail(email).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
		);

		if(comment.isDeleted()) {
			throw new IllegalArgumentException("삭제된 댓글은 수정이 불가능합니다.");
		}

		if (!comment.getUser().getEmail().equals(email)) {
			throw new IllegalArgumentException("본인이 작성한 댓글만 수정이 가능합니다.");
		}

		comment.updateContent(request.getContent());
		commentRepository.save(comment);

		boolean likedByLoginUser = commentLikeRepository.existsByCommentAndUser(comment, user);

		return CommentResponseDto.from(comment, likedByLoginUser);
	}

	@Transactional
	public boolean likeComment(Long commentId) {
		String email = SecurityUtil.getLoginEmail();

		User user = userRepository.findByEmail(email).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

		if(comment.isDeleted()) {
			throw new IllegalArgumentException("삭제된 댓글에는 좋아요를 할 수 없습니다.");
		}

		Optional<CommentLike> existing = commentLikeRepository.findByCommentAndUser(comment, user);

		if(existing.isPresent()) { // 이미 좋아요를 눌렀으면
			commentLikeRepository.delete(existing.get());
			comment.decreaseLikeCount();
			commentRepository.save(comment);

			return false;
		} else {
			CommentLike commentLike = CommentLike.builder()
				.comment(comment)
				.user(user)
				.build();

			comment.increaseLikeCount();
			commentRepository.save(comment);
			commentLikeRepository.save(commentLike);

			return true;
		}
	}
}
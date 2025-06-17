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
import org.brokong.morakbackend.comment.query.CommentQueryRepository;
import org.brokong.morakbackend.comment.repository.CommentRepository;
import org.brokong.morakbackend.global.Security.SecurityUtil;
import org.brokong.morakbackend.like.Repository.CommentLikeRepository;
import org.brokong.morakbackend.like.entity.CommentLike;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.repository.PostRepository;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final CommentQueryRepository commentQueryRepository;

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

		return CommentResponseDto.from(comment, false, false);
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

		return CommentResponseDto.from(comment, likedByLoginUser, commentRepository.existsByParentComment(comment));
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

	public Page<CommentResponseDto> getRootComments(Long postId, int page, int size, String sortBy) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Comment> rootComments = commentQueryRepository.findRootCommentsByPostWithSorting(postId, pageable, sortBy);

		Optional<String> optionalEmail = SecurityUtil.getOptionalLoginEmail();

		if(optionalEmail.isEmpty()) {
			// 비로그인시 모든 댓글 likedByLoginUser = false
			return rootComments.map(comment -> CommentResponseDto.from(comment, false, commentRepository.existsByParentComment(comment)));
		}

		// 로그인 유저면
		String email = optionalEmail.get();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		// 댓글 ID 추출
		List<Long> commentIds = rootComments.getContent().stream()
			.map(Comment::getId)
			.toList();

		// 로그인 유저가 좋아요 누른 댓글 ID 추출
		Set<Long> likedCommentIds = commentLikeRepository.findAllByCommentIdInAndUser(commentIds, user)
														 .stream()
														 .map(like -> like.getComment().getId())
														 .collect(Collectors.toSet());

		return rootComments.map(comment ->
									CommentResponseDto.from(comment, likedCommentIds.contains(comment.getId()), commentRepository.existsByParentComment(comment)));
	}

	public Page<CommentResponseDto> getReplies(Long parentId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Comment> replies = commentQueryRepository.findRepliesByParentComment(parentId, pageable);

		Optional<String> optionalEmail = SecurityUtil.getOptionalLoginEmail();

		if(optionalEmail.isEmpty()) {
			// 비로그인시 모든 댓글 likedByLoginUser = false
			return replies.map(comment -> CommentResponseDto.from(comment, false, false));
		}

		// 로그인 유저면
		String email = optionalEmail.get();

		User user = userRepository.findByEmail(email)
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		// 댓글 ID 추출
		List<Long> commentIds = replies.getContent().stream()
											.map(Comment::getId)
											.toList();

		// 로그인 유저가 좋아요 누른 댓글 ID 추출
		Set<Long> likedCommentIds = commentLikeRepository.findAllByCommentIdInAndUser(commentIds, user)
														 .stream()
														 .map(like -> like.getComment().getId())
														 .collect(Collectors.toSet());


		return replies.map(comment ->
									CommentResponseDto.from(comment, likedCommentIds.contains(comment.getId()), commentRepository.existsByParentComment(comment)));
	}

	public CommentResponseDto getCommentById(Long commentId) {
		Optional<String> optionalEmail = SecurityUtil.getOptionalLoginEmail();

		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		if(optionalEmail.isEmpty()) {
			// 비로그인시 모든 댓글 likedByLoginUser = false
			return CommentResponseDto.from(comment, false, false);
		}

		// 로그인 유저면
		String email = optionalEmail.get();

		User user = userRepository.findByEmail(email)
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		// 댓글 ID 추출
		Long commentIds = comment.getId();

		// 로그인 유저가 좋아요 누른 댓글 ID 추출
		Set<Long> likedCommentIds = commentLikeRepository.findLikedCommentIdsByUser(user);

		return CommentResponseDto.from(comment, likedCommentIds.contains(commentIds), commentRepository.existsByParentComment(comment));
	}
}
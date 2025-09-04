package org.brokong.morakbackend.comment.service;

import java.util.List;
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
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.global.enums.SortType;
import org.brokong.morakbackend.global.request.ReportRequestDto;
import org.brokong.morakbackend.like.repository.CommentLikeRepository;
import org.brokong.morakbackend.like.entity.CommentLike;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.repository.PostRepository;
import org.brokong.morakbackend.report.entity.CommentReport;
import org.brokong.morakbackend.report.repository.CommentReportRepository;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	private final CommentReportRepository commentReportRepository;

	@Transactional
	public CommentResponseDto createComment(CommentRequestDto request, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
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

		// 게시글의 댓글 수 증가
		post.increaseCommentCount();
		postRepository.save(post);

		return CommentResponseDto.from(comment, false);
	}

	@Transactional
	public void deleteComment(Long commentId, UserPrincipal userPrincipal) {

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
		);

		if(comment.isDeleted()) {
			throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
		}

		if (!comment.getUser().getEmail().equals(userPrincipal.getEmail())) {
			throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
		}

		comment.delete();
		commentRepository.save(comment);

		// 게시글의 댓글 수 감소
		Post post = comment.getPost();
		post.decreaseCommentCount();
		postRepository.save(post);
	}

	@Transactional
	public CommentResponseDto updateComment(Long commentId, CommentUpdateRequestDto request, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
		);

		if(comment.isDeleted()) {
			throw new IllegalArgumentException("삭제된 댓글은 수정이 불가능합니다.");
		}

		if (!comment.getUser().getEmail().equals(userPrincipal.getEmail())) {
			throw new IllegalArgumentException("본인이 작성한 댓글만 수정이 가능합니다.");
		}

		comment.updateContent(request.getContent());
		commentRepository.save(comment);

		boolean likedByLoginUser = commentLikeRepository.existsByCommentAndUser(comment, user);

		return CommentResponseDto.from(comment, likedByLoginUser, commentRepository.existsByParentComment(comment));
	}

	@Transactional
	public boolean likeComment(Long commentId, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
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

	public Page<CommentResponseDto> getRootComments(Long postId, int page, int size, SortType sortBy, UserPrincipal userPrincipal) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Comment> rootComments = commentQueryRepository.findRootCommentsByPostWithSorting(postId, pageable, sortBy);

		if (userPrincipal == null) {
			return rootComments.map(comment ->
										CommentResponseDto.from(comment, false, commentRepository.existsByParentComment(comment)));
		}

		// 로그인 유저면
		User user = userRepository.findByEmail(userPrincipal.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		List<Long> commentIds = rootComments.getContent().stream()
											.map(Comment::getId)
											.toList();

		// N+1 해결: 최적화된 메서드 사용
		Set<Long> likedCommentIds = commentLikeRepository
			.findLikedCommentIdsByCommentIdsAndUser(commentIds, user);

		return rootComments.map(comment ->
									CommentResponseDto.from(
										comment,
										likedCommentIds.contains(comment.getId()),
										commentRepository.existsByParentComment(comment)
									));
	}

	public Page<CommentResponseDto> getReplies(Long parentId, int page, int size, UserPrincipal userPrincipal) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Comment> replies = commentQueryRepository.findRepliesByParentComment(parentId, pageable);

		if (userPrincipal == null) {
			// 비로그인시 모든 댓글 likedByLoginUser = false
			return replies.map(comment -> CommentResponseDto.from(comment, false, false));
		}

		// 로그인 유저면
		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		// 댓글 ID 추출
		List<Long> commentIds = replies.getContent().stream()
											.map(Comment::getId)
											.toList();

		// N+1 해결: 이미 최적화된 메서드 사용
		Set<Long> likedCommentIds = commentLikeRepository.findLikedCommentIdsByCommentIdsAndUser(commentIds, user);


		return replies.map(comment ->
									CommentResponseDto.from(comment, likedCommentIds.contains(comment.getId()), false));
	}

	public CommentResponseDto getCommentById(Long commentId, UserPrincipal userPrincipal) {

		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		if (userPrincipal == null) {
			// 비로그인시 모든 댓글 likedByLoginUser = false
			return CommentResponseDto.from(comment, false, false);
		}

		// 로그인 유저면
		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		// 로그인 유저가 좋아요 누른 댓글 ID 추출
		// 수정 후 - 해당 댓글만 체크
		boolean likedByUser = commentLikeRepository.existsByCommentAndUser(comment, user);
		boolean hasChildren = commentRepository.existsByParentComment(comment);

		return CommentResponseDto.from(comment, likedByUser, hasChildren);
	}

	@Transactional
	public void reportComment(Long commentId, ReportRequestDto requestDto, UserPrincipal userPrincipal) {
		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

		Comment comment = commentRepository.findById(commentId)
										   .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

		if (commentReportRepository.existsByUserAndComment(user, comment)) {
			throw new IllegalArgumentException("이미 신고한 댓글입니다.");
		}

		CommentReport commentReport = new CommentReport(user, comment, requestDto.getReason());
		commentReportRepository.save(commentReport);
	}
}

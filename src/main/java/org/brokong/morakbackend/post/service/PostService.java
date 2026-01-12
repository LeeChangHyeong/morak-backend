package org.brokong.morakbackend.post.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.global.enums.SortType;
import org.brokong.morakbackend.global.request.ReportRequestDto;
import org.brokong.morakbackend.like.repository.PostLikeRepository;
import org.brokong.morakbackend.like.entity.PostLike;
import org.brokong.morakbackend.post.dto.PostResponseDto;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.query.PostQueryRepository;
import org.brokong.morakbackend.post.repository.PostRepository;
import org.brokong.morakbackend.report.entity.PostReport;
import org.brokong.morakbackend.report.repository.PostReportRepository;
import org.brokong.morakbackend.user.entity.User;
import org.brokong.morakbackend.user.repository.UserRepository;
import org.brokong.morakbackend.friend.repository.BlockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostQueryRepository postQueryRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostReportRepository postReportRepository;
	private final BlockRepository blockRepository;

	@Transactional
	public PostResponseDto createPost(String content, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Post post = Post.builder()
						.user(user)
						.content(content)
						.build();

		postRepository.save(post);

		return PostResponseDto.from(post, false, true); // 새로 작성한 게시글은 좋아요 안 누름
	}

	@Transactional
	public void deletePost(Long postId, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (!post.getUser().equals(user)) {
			throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
		}

		postRepository.deleteById(postId);
	}

	// 로그인하지 않은 사용자도 조회 가능
	@Transactional
	public PostResponseDto getPost(Long postId) {
		Post post = postRepository.findById(postId)
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		post.increaseViewCount();
		postRepository.save(post);

		return PostResponseDto.from(post);
	}

	// 로그인한 사용자의 게시글 조회 (좋아요 상태 포함, 차단한 사용자 게시글 접근 차단)
	@Transactional
	public PostResponseDto getPost(Long postId, UserPrincipal userPrincipal) {
		Post post = postRepository.findById(postId)
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		// 차단한 사용자의 게시글인지 확인
		boolean isBlocked = blockRepository.existsByBlockerAndBlocked(user, post.getUser());
		if (isBlocked) {
			throw new IllegalArgumentException("차단한 사용자의 게시글입니다.");
		}

		post.increaseViewCount();
		postRepository.save(post);

		boolean likedByUser = postLikeRepository.existsByPostAndUser(post, user);
		boolean wroteByUser = post.getUser().equals(user);

		return PostResponseDto.from(post, likedByUser, wroteByUser);
	}

	// 로그인하지 않은 사용자도 목록 조회 가능
	public Page<PostResponseDto> getPostList(int page, int size, SortType sortBy) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postQueryRepository.findAllWithSorting(pageable, sortBy);

		return posts.map(post -> PostResponseDto.from(post));
	}

	// 로그인한 사용자의 목록 조회 (좋아요 상태 포함, 차단한 사용자 게시글 제외)
	public Page<PostResponseDto> getPostList(int page, int size, SortType sortBy, UserPrincipal userPrincipal) {
		System.out.println("=== 디버깅: 로그인한 사용자 전체조회 시작");

		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		System.out.println("=== 디버깅: 사용자 ID = " + user.getId() + ", 이메일 = " + user.getEmail());

		// 내가 차단한 사용자들의 ID 목록 조회
		List<Long> blockedUserIds = blockRepository.findBlockedUserIdsByBlockerId(user.getId());
		System.out.println("=== 디버깅: 차단한 사용자 IDs = " + blockedUserIds);

		Pageable pageable = PageRequest.of(page, size);
		// 차단한 사용자 게시글 제외하고 조회
		Page<Post> posts = postQueryRepository.findAllWithSortingExcludingBlockedUsers(pageable, sortBy, blockedUserIds);

		// 🔥 핵심: 배치로 좋아요 정보 조회
		List<Long> postIds = posts.getContent().stream()
								  .map(Post::getId)
								  .collect(Collectors.toList());

		System.out.println("=== 디버깅: 조회된 게시글 IDs = " + postIds);

		List<Long> likedPostIdsFromDb = postLikeRepository.findLikedPostIdsByUserAndPostIds(user, postIds);
		System.out.println("=== 디버깅: 좋아요한 게시글 IDs = " + likedPostIdsFromDb);

		Set<Long> likedPostIds = new HashSet<>(likedPostIdsFromDb);

		return posts.map(post -> {
			boolean likedByUser = likedPostIds.contains(post.getId()); // 메모리에서 확인
			boolean wroteByUser = post.getUser().getId().equals(user.getId());
			System.out.println("=== 디버깅: 게시글 " + post.getId() + " 좋아요 상태 = " + likedByUser + ", 작성자 여부 = " + wroteByUser);
			return PostResponseDto.from(post, likedByUser, wroteByUser);
		});
	}

	@Transactional
	public boolean likePost(Long postId, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);

		Post post = postRepository.findById(postId).orElseThrow(
			() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);

		if (existing.isPresent()) { // 이미 좋아요를 눌렀으면
			postLikeRepository.delete(existing.get());
			post.decreaseLikeCount();
			postRepository.save(post);

			return false;
		} else {
			PostLike postLike = PostLike.builder()
										.post(post)
										.user(user)
										.build();

			post.increaseLikeCount();
			postRepository.save(post);
			postLikeRepository.save(postLike);

			return true;
		}
	}

	@Transactional
	public PostResponseDto updatePost(Long postId, String content, UserPrincipal userPrincipal) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if (!post.getUser().equals(user)) {
			throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
		}

		post.updateContent(content);
		postRepository.save(post);

		// 좋아요 상태 포함
		boolean likedByUser = postLikeRepository.existsByPostAndUser(post, user);
		boolean wroteByUser = post.getUser().equals(user);

		return PostResponseDto.from(post, likedByUser, wroteByUser);
	}

	public Page<PostResponseDto> getMyPostList(int page, int size, SortType sortBy, UserPrincipal userPrincipal) {
		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		Pageable pageable = PageRequest.of(page, size);

		Page<Post> posts = postQueryRepository.findAllByUserWithSorting(pageable, sortBy, user.getId());

		// 내 게시글도 좋아요 상태 포함 (배치 조회)
		List<Long> postIds = posts.getContent().stream()
								  .map(Post::getId)
								  .collect(Collectors.toList());

		Set<Long> likedPostIds = new HashSet<>(
			postLikeRepository.findLikedPostIdsByUserAndPostIds(user, postIds)
		);

		return posts.map(post -> {
			boolean likedByUser = likedPostIds.contains(post.getId());
			boolean wroteByUser = post.getUser().equals(user);
			return PostResponseDto.from(post, likedByUser, wroteByUser);
		});
	}

	@Transactional
	public void reportPost(Long postId, ReportRequestDto requestDto, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if (postReportRepository.existsByPostAndUser(post, user)) {
			throw new IllegalArgumentException("이미 신고한 게시글입니다.");
		}

		PostReport postReport = new PostReport(user, post, requestDto.getReason());
		postReportRepository.save(postReport);
	}
}

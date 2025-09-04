package org.brokong.morakbackend.post.service;

import java.util.Optional;
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
	private final PostLikeRepository postLikeRepostory;
	private final PostReportRepository postReportRepository;

	@Transactional
	public PostResponseDto createPost(String content, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Post post = Post.builder()
						.user(user)
						.content(content)
						.build();

		postRepository.save(post);

		return PostResponseDto.from(post, false); // 새로 작성한 게시글은 좋아요 안 누름
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

		return PostResponseDto.from(post, false);
	}

	// 로그인한 사용자의 게시글 조회 (좋아요 상태 포함)
	@Transactional
	public PostResponseDto getPost(Long postId, UserPrincipal userPrincipal) {
		Post post = postRepository.findById(postId)
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		post.increaseViewCount();
		postRepository.save(post);

		boolean likedByUser = postLikeRepostory.existsByPostAndUser(post, user);

		return PostResponseDto.from(post, likedByUser);
	}

	// 로그인하지 않은 사용자도 목록 조회 가능
	public Page<PostResponseDto> getPostList(int page, int size, SortType sortBy) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postQueryRepository.findAllWithSorting(pageable, sortBy);

		return posts.map(post -> PostResponseDto.from(post, false));
	}

	// 로그인한 사용자의 목록 조회 (좋아요 상태 포함)
	public Page<PostResponseDto> getPostList(int page, int size, SortType sortBy, UserPrincipal userPrincipal) {
		User user = userRepository.findByEmail(userPrincipal.getEmail())
								  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postQueryRepository.findAllWithSorting(pageable, sortBy);

		return posts.map(post -> {
			boolean likedByUser = postLikeRepostory.existsByPostAndUser(post, user);
			return PostResponseDto.from(post, likedByUser);
		});
	}

	@Transactional
	public boolean likePost(Long postId, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(
			() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
		);

		Post post = postRepository.findById(postId).orElseThrow(
			() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		Optional<PostLike> existing = postLikeRepostory.findByPostAndUser(post, user);

		if (existing.isPresent()) { // 이미 좋아요를 눌렀으면
			postLikeRepostory.delete(existing.get());
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
			postLikeRepostory.save(postLike);

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

		return PostResponseDto.from(post);
	}

	public Page<PostResponseDto> getMyPostList(int page, int size, SortType sortBy, UserPrincipal userPrincipal) {
		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		Pageable pageable = PageRequest.of(page, size);

		Page<Post> posts = postQueryRepository.findAllByUserWithSorting(pageable, sortBy, user.getId());

		return posts.map(PostResponseDto::from);
	}

	@Transactional
	public void reportPost(Long postId, ReportRequestDto requestDto, UserPrincipal userPrincipal) {

		User user = userRepository.findByEmail(userPrincipal.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
		Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

		if(postReportRepository.existsByPostAndUser(post, user)) {
			throw new IllegalArgumentException("이미 신고한 게시글입니다.");
		}

		PostReport postReport = new PostReport(user, post, requestDto.getReason());
		postReportRepository.save(postReport);
	}
}

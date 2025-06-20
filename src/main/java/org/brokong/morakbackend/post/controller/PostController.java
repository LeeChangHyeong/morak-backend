package org.brokong.morakbackend.post.controller;

import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.report.dto.PostReportRequestDto;
import org.brokong.morakbackend.post.dto.PostResponseDto;
import org.brokong.morakbackend.post.service.PostService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/posts")
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<ResponseDto<PostResponseDto>> createPost(@RequestBody PostReportRequestDto requestDto) {

		PostResponseDto responseDto = postService.createPost(requestDto.getContent());

		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("게시글이 성공적으로 작성되었습니다.", responseDto));
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<ResponseDto<Void>> deletePost(@PathVariable Long postId) {

		postService.deletePost(postId);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글이 성공적으로 삭제되었습니다.", null));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<ResponseDto<PostResponseDto>> getPost(@PathVariable Long postId) {
		PostResponseDto responseDto = postService.getPost(postId);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글이 성공적으로 조회되었습니다.", responseDto));
	}

	// createdAt, likeCount, viewCount 가능
	@GetMapping
	public ResponseEntity<ResponseDto<Page<PostResponseDto>>> getPosts(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sortBy) {

		Page<PostResponseDto> posts = postService.getPostList(page - 1, size, sortBy);
		return ResponseEntity.ok(new ResponseDto<>("게시글 목록 조회 성공", posts));
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<ResponseDto<Boolean>> likePost(@PathVariable Long postId) {
		Boolean liked = postService.likePost(postId);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글 좋아요 상태변경", liked));
	}

	@PutMapping("/{postId}")
	public ResponseEntity<ResponseDto<PostResponseDto>> updatePost(
		@PathVariable Long postId,
		@RequestBody PostReportRequestDto requestDto
	) {
		PostResponseDto responseDto = postService.updatePost(postId, requestDto.getContent());

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글이 성공적으로 수정되었습니다.", responseDto));
	}

	@GetMapping("/me")
	public ResponseEntity<ResponseDto<Page<PostResponseDto>>> getMyPosts(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sortBy) {

		Page<PostResponseDto> posts = postService.getMyPostList(page - 1, size, sortBy);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("내 게시글 목록 조회 성공", posts));
	}

	@PostMapping("/{postId}/report")
	public ResponseEntity<ResponseDto<Void>> reportPost(@PathVariable Long postId, @RequestBody PostReportRequestDto requestDto) {
		postService.reportPost(postId, requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("게시글이 성공적으로 신고되었습니다.", null));
	}
}

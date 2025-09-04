package org.brokong.morakbackend.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.brokong.morakbackend.global.enums.SortType;
import org.brokong.morakbackend.global.request.ReportRequestDto;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.post.dto.PostRequestDto;
import org.brokong.morakbackend.post.dto.PostResponseDto;
import org.brokong.morakbackend.post.service.PostService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/posts")
@Tag(name = "Post", description = "게시글 관련 API")
public class PostController {

	private final PostService postService;

	@Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "게시글 작성 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping
	public ResponseEntity<ResponseDto<PostResponseDto>> createPost(
		@Parameter(description = "게시글 작성 요청 정보", required = true)
		@RequestBody PostRequestDto requestDto,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		PostResponseDto responseDto = postService.createPost(requestDto.getContent(), userPrincipal);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("게시글이 성공적으로 작성되었습니다.", responseDto));
	}

	@Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping("/{postId}")
	public ResponseEntity<ResponseDto<Void>> deletePost(
		@Parameter(description = "삭제할 게시글 ID", required = true, example = "1")
		@PathVariable Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		postService.deletePost(postId, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글이 성공적으로 삭제되었습니다.", null));
	}

	@Operation(summary = "게시글 조회", description = "특정 게시글을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시글 조회 성공")
	})
	@GetMapping("/{postId}")
	public ResponseEntity<ResponseDto<PostResponseDto>> getPost(
		@Parameter(description = "조회할 게시글 ID", required = true, example = "1")
		@PathVariable Long postId, @AuthenticationPrincipal UserPrincipal userPrincipal) {

		PostResponseDto responseDto;

		if (userPrincipal != null) {
			// 로그인한 사용자 - 좋아요 상태 포함
			responseDto = postService.getPost(postId, userPrincipal);
		} else {
			// 로그인하지 않은 사용자
			responseDto = postService.getPost(postId);
		}

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글이 성공적으로 조회되었습니다.", responseDto));
	}

	@Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
	})
	@GetMapping
	public ResponseEntity<ResponseDto<Page<PostResponseDto>>> getPosts(
		@Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
		@RequestParam(defaultValue = "1") int page,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(defaultValue = "10") int size,
		@Parameter(description = "정렬 기준", example = "CREATED_AT_DESC")
		@RequestParam(defaultValue = "CREATED_AT_DESC") SortType sortBy) {

		Page<PostResponseDto> posts = postService.getPostList(page - 1, size, sortBy);
		return ResponseEntity.ok(new ResponseDto<>("게시글 목록 조회 성공", posts));
	}

	@Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가하거나 취소합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시글 좋아요 상태 변경 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{postId}/like")
	public ResponseEntity<ResponseDto<Boolean>> likePost(
		@Parameter(description = "좋아요할 게시글 ID", required = true, example = "1")
		@PathVariable Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		Boolean liked = postService.likePost(postId, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글 좋아요 상태변경", liked));
	}

	@Operation(summary = "게시글 수정", description = "특정 게시글의 내용을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시글 수정 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PutMapping("/{postId}")
	public ResponseEntity<ResponseDto<PostResponseDto>> updatePost(
		@Parameter(description = "수정할 게시글 ID", required = true, example = "1")
		@PathVariable Long postId,
		@Parameter(description = "게시글 수정 요청 정보", required = true)
		@RequestBody PostRequestDto requestDto,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		PostResponseDto responseDto = postService.updatePost(postId, requestDto.getContent(), userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("게시글이 성공적으로 수정되었습니다.", responseDto));
	}

	@Operation(summary = "내 게시글 목록 조회", description = "현재 로그인된 사용자가 작성한 게시글 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "내 게시글 목록 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping("/me")
	public ResponseEntity<ResponseDto<Page<PostResponseDto>>> getMyPosts(
		@Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
		@RequestParam(defaultValue = "1") int page,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(defaultValue = "10") int size,
		@Parameter(description = "정렬 기준", example = "CREATED_AT_DESC")
		@RequestParam(defaultValue = "CREATED_AT_DESC") SortType sortBy,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		Page<PostResponseDto> posts;

		if (userPrincipal != null) {
			// 로그인한 사용자 - 좋아요 상태 포함
			posts = postService.getPostList(page - 1, size, sortBy, userPrincipal);
		} else {
			// 로그인하지 않은 사용자
			posts = postService.getPostList(page - 1, size, sortBy);
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("내 게시글 목록 조회 성공", posts));
	}

	@Operation(summary = "게시글 신고", description = "특정 게시글을 신고합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "게시글 신고 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{postId}/report")
	public ResponseEntity<ResponseDto<Void>> reportPost(
		@Parameter(description = "신고할 게시글 ID", required = true, example = "1")
		@PathVariable Long postId,
		@Parameter(description = "신고 요청 정보", required = true)
		@RequestBody ReportRequestDto requestDto,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		postService.reportPost(postId, requestDto, userPrincipal);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("게시글이 성공적으로 신고되었습니다.", null));
	}
}
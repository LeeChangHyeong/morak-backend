package org.brokong.morakbackend.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.comment.dto.CommentRequestDto;
import org.brokong.morakbackend.comment.dto.CommentResponseDto;
import org.brokong.morakbackend.comment.dto.CommentUpdateRequestDto;
import org.brokong.morakbackend.comment.service.CommentService;
import org.brokong.morakbackend.global.enums.SortType;
import org.brokong.morakbackend.global.request.ReportRequestDto;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.brokong.morakbackend.global.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/comments")
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

	private final CommentService commentService;

	@Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "댓글 작성 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping
	public ResponseEntity<ResponseDto<CommentResponseDto>> createComment(
		@Parameter(description = "댓글 작성 요청 정보", required = true)
		@RequestBody CommentRequestDto request,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		CommentResponseDto responseDto = commentService.createComment(request, userPrincipal);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("댓글 작성 성공", responseDto));
	}

	@Operation(summary = "댓글 조회", description = "특정 댓글을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping("/{commentId}")
	public ResponseEntity<ResponseDto<CommentResponseDto>> getCommentById(
		@Parameter(description = "조회할 댓글 ID", required = true, example = "1")
		@PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		CommentResponseDto comment = commentService.getCommentById(commentId, userPrincipal);
		return ResponseEntity.ok(new ResponseDto<>("댓글 조회 성공", comment));
	}

	@Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<ResponseDto<Void>> deleteComment(
		@Parameter(description = "삭제할 댓글 ID", required = true, example = "1")
		@PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		commentService.deleteComment(commentId, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 삭제 성공", null));
	}

	@Operation(summary = "댓글 수정", description = "특정 댓글의 내용을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 수정 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PutMapping("/{commentId}")
	public ResponseEntity<ResponseDto<CommentResponseDto>> updateComment(
		@Parameter(description = "수정할 댓글 ID", required = true, example = "1")
		@PathVariable Long commentId,
		@Parameter(description = "댓글 수정 요청 정보", required = true)
		@RequestBody CommentUpdateRequestDto request,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		CommentResponseDto responseDto = commentService.updateComment(commentId, request, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 수정 성공", responseDto));
	}

	@Operation(summary = "댓글 좋아요", description = "댓글에 좋아요를 추가하거나 취소합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 좋아요 상태 변경 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{commentId}/like")
	public ResponseEntity<ResponseDto<Boolean>> likeComment(
		@Parameter(description = "좋아요할 댓글 ID", required = true, example = "1")
		@PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		boolean liked = commentService.likeComment(commentId, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 좋아요 상태 변경", liked));
	}

	@Operation(summary = "루트 댓글 조회", description = "게시글의 최상위 댓글들을 페이징하여 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "루트 댓글 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping("/root")
	public ResponseEntity<ResponseDto<Page<CommentResponseDto>>> getRootComments(
		@Parameter(description = "게시글 ID", required = true, example = "1")
		@RequestParam Long postId,
		@Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
		@RequestParam(defaultValue = "1") int page,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(defaultValue = "10") int size,
		@Parameter(description = "정렬 기준", example = "CREATED_AT_ASC")
		@RequestParam(defaultValue = "CREATED_AT_ASC") SortType sortBy,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Page<CommentResponseDto> comments = commentService.getRootComments(postId, page - 1, size, sortBy, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("루트 댓글 조회 성공", comments));
	}

	@Operation(summary = "대댓글 조회", description = "특정 댓글의 대댓글들을 페이징하여 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "대댓글 조회 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@GetMapping("/{parentId}/replies")
	public ResponseEntity<ResponseDto<Page<CommentResponseDto>>> getReplies(
		@Parameter(description = "부모 댓글 ID", required = true, example = "1")
		@PathVariable Long parentId,
		@Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
		@RequestParam(defaultValue = "1") int page,
		@Parameter(description = "페이지 크기", example = "10")
		@RequestParam(defaultValue = "10") int size,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Page<CommentResponseDto> replies = commentService.getReplies(parentId, page - 1, size, userPrincipal);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("대댓글 조회 성공", replies));
	}

	@Operation(summary = "댓글 신고", description = "특정 댓글을 신고합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "댓글 신고 성공")
	})
	@SecurityRequirement(name = "bearer-jwt")
	@PostMapping("/{commentId}/report")
	public ResponseEntity<ResponseDto<Void>> reportComment(
		@Parameter(description = "신고할 댓글 ID", required = true, example = "1")
		@PathVariable Long commentId,
		@Parameter(description = "신고 요청 정보", required = true)
		@RequestBody ReportRequestDto requestDto,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {

		commentService.reportComment(commentId, requestDto, userPrincipal);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("댓글이 성공적으로 신고되었습니다.", null));
	}
}
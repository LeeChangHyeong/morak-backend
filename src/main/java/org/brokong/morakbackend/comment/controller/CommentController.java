package org.brokong.morakbackend.comment.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.comment.dto.CommentRequestDto;
import org.brokong.morakbackend.comment.dto.CommentResponseDto;
import org.brokong.morakbackend.comment.dto.CommentUpdateRequestDto;
import org.brokong.morakbackend.comment.service.CommentService;
import org.brokong.morakbackend.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/comments")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<ResponseDto<CommentResponseDto>> createComment(@RequestBody CommentRequestDto request) {
		CommentResponseDto responseDto = commentService.createComment(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto<>("댓글 작성 성공", responseDto));
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<ResponseDto<Void>> deleteComment(@PathVariable Long commentId) {
		commentService.deleteComment(commentId);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 삭제 성공", null));
	}

	@GetMapping
	public ResponseEntity<ResponseDto<List<CommentResponseDto>>> getComments(@RequestParam Long postId) {
		List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 조회 성공", comments));
	}

	@PostMapping("/{commentId}")
	public ResponseEntity<ResponseDto<CommentResponseDto>> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequestDto request) {
		CommentResponseDto responseDto = commentService.updateComment(commentId, request);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 수정 성공", responseDto));
	}

	@PostMapping("/{commentId}/like")
	public ResponseEntity<ResponseDto<Boolean>> likeComment(@PathVariable Long commentId) {
		boolean liked = commentService.likeComment(commentId);

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>("댓글 좋아요 상태 변경", liked));
	}

}

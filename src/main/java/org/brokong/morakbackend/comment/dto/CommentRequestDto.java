package org.brokong.morakbackend.comment.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "댓글 작성 요청")
public class CommentRequestDto {
	@Schema(description = "게시글 ID", example = "1", required = true)
	private Long postId;

	@Schema(description = "댓글 내용", example = "좋은 게시글이네요!", required = true)
	private String content;

	@Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "1")
	private Long parentId;
}

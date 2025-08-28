package org.brokong.morakbackend.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "댓글 수정 요청")
public class CommentUpdateRequestDto {
	@Schema(description = "수정할 댓글 내용", example = "수정된 댓글입니다.", required = true)
	private String content;
}
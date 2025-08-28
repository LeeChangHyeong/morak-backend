package org.brokong.morakbackend.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "게시글 요청")
public class PostRequestDto {

	@Schema(description = "게시글 내용", example = "안녕하세요, 새로운 게시글입니다.", required = true)
	private String content;

	// getter, setter
}

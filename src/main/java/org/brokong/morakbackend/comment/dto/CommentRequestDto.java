package org.brokong.morakbackend.comment.dto;


import lombok.Getter;

@Getter
public class CommentRequestDto {
	private Long postId;
	private Long parentId;
	private String content;
}

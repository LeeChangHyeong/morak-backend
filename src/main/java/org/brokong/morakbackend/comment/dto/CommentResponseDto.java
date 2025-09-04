package org.brokong.morakbackend.comment.dto;

import lombok.Getter;
import org.brokong.morakbackend.comment.entity.Comment;

@Getter
public class CommentResponseDto {

	private Long id;
	private String content;
	private String nickname;
	private Long userId;
	private Long postId;
	private Long parentId;
	private boolean isDeleted;
	private long likeCount;
	private boolean likedByLoginUser;
	private String createdAt;
	private String modifiedAt;
	private boolean hasChildren;

	// 로그인 사용자 없을 때 (기본값)
	public static CommentResponseDto from(Comment comment, boolean hasChildren) {
		return from(comment, false, hasChildren);
	}

	// 로그인 사용자 있을 때 (좋아요 상태 포함)
	public static CommentResponseDto from(Comment comment, boolean likedByLoginUser, boolean hasChildren) {
		CommentResponseDto dto = new CommentResponseDto();

		dto.id = comment.getId();
		dto.content = comment.getContent();
		dto.nickname = comment.getUser().getNickname();
		dto.userId = comment.getUser().getId();
		dto.postId = comment.getPost().getId();
		dto.parentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
		dto.isDeleted = comment.isDeleted();
		dto.likeCount = comment.getLikeCount();
		dto.likedByLoginUser = likedByLoginUser;
		dto.createdAt = comment.getCreatedAt().toString();
		dto.modifiedAt = comment.getModifiedAt().toString();
		dto.hasChildren = hasChildren;

		return dto;
	}
}
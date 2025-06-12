package org.brokong.morakbackend.comment.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.brokong.morakbackend.comment.entity.Comment;

@Getter
public class CommentResponseDto {

	private Long id;
	private String content;
	private String nickname;
	private Long userId; // 작성자 id
	private Long postId;
	private Long parentId;
	private boolean isDeleted; // 삭제되면 true로 변경
	private long likeCount;
	private boolean likedByLoginUser; // 로그인 유저가 좋아요 눌렀는지
	private String createdAt;
	private String modifiedAt;
	private List<CommentResponseDto> children = new ArrayList<>();

	public static CommentResponseDto from(Comment comment, boolean likedByLoginUser){
		CommentResponseDto dto = new CommentResponseDto();
		dto.id = comment.getId();
		dto.content = comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent();
		dto.nickname = comment.getUser().getNickname();
		dto.userId = comment.getUser().getId();
		dto.postId = comment.getPost().getId();
		dto.parentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
		dto.isDeleted = comment.isDeleted();
		dto.likeCount = comment.getLikeCount();
		dto.likedByLoginUser = likedByLoginUser;
		dto.createdAt = comment.getCreatedAt().toString();
		dto.modifiedAt = comment.getModifiedAt().toString();
		// children은 나중에 재귀적으로 세팅
		return dto;
	}
}

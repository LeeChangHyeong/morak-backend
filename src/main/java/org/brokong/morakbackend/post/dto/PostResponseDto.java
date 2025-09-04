package org.brokong.morakbackend.post.dto;

import lombok.Getter;
import org.brokong.morakbackend.post.entity.Post;

@Getter
public class PostResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private Long likeCount;
    private Long viewCount;
    private String createdAt;
    private String modifiedAt;
    private boolean likedByLoginUser; // 로그인 유저가 좋아요 눌렀는지
    private Long commentCount;

    // 로그인 사용자 없을 때 (게시글 단건 조회, 목록 조회)
    public static PostResponseDto from(Post post) {
        return from(post, false);
    }

    // 로그인 사용자 있을 때 (좋아요 상태 포함)
    public static PostResponseDto from(Post post, boolean likedByLoginUser) {
        PostResponseDto dto = new PostResponseDto();

        dto.id = post.getId();
        dto.content = post.getContent();
        dto.nickname = post.getUser().getNickname();
        dto.likeCount = post.getLikeCount();
        dto.viewCount = post.getViewCount();
        dto.createdAt = post.getCreatedAt().toString();
        dto.modifiedAt = post.getModifiedAt().toString();
        dto.likedByLoginUser = likedByLoginUser;
        dto.commentCount = post.getCommentCount();

        return dto;
    }

}

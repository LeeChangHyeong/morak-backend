package org.brokong.morakbackend.post.dto;

import lombok.Getter;
import org.brokong.morakbackend.post.entity.Post;

@Getter
public class PostResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private String createdAt;
    private String modifiedAt;

    public static PostResponseDto from(Post post) {
        PostResponseDto dto = new PostResponseDto();

        dto.id = post.getId();
        dto.content = post.getContent();
        dto.nickname = post.getUser().getNickname();
        dto.createdAt = post.getCreatedAt().toString();
        dto.modifiedAt = post.getModifiedAt().toString();

        return dto;
    }

}

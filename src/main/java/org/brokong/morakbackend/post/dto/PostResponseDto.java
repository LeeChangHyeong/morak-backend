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

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.nickname = post.getUser().getNickname();
        this.createdAt = post.getCreatedAt().toString();
        this.modifiedAt = post.getModifiedAt().toString();
    }

}

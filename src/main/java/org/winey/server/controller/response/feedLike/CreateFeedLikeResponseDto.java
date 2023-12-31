package org.winey.server.controller.response.feedLike;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateFeedLikeResponseDto {
    private Long feedId;
    private Boolean isLiked;
    private Long likes;

    public static CreateFeedLikeResponseDto of(Long feedId, Boolean isLiked, Long likes) {
        return new CreateFeedLikeResponseDto(feedId, isLiked, likes);
    }
}

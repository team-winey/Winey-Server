package org.winey.server.controller.response.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.controller.response.feedLike.CreateFeedLikeResponseDto;
import org.winey.server.domain.user.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCommentResponseDto {
    private Long feedId;
    private Long commentCounter;
    private User author;

    public static CreateCommentResponseDto of(Long feedId, Long commentCounter,User author) {
        return new CreateCommentResponseDto(feedId, commentCounter,author);
    }
}

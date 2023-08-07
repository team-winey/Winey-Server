package org.winey.server.controller.response.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.user.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCommentResponseDto {
    private Long commentId;
    private User author;
    private String content;
    private LocalDate createdAt;

    public static GetCommentResponseDto of(Long commentId,User author, String content, LocalDate createdAt) {
        return new GetCommentResponseDto(commentId, author, content, createdAt);
    }
}

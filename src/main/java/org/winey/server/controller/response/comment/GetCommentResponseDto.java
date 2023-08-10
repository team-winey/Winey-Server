package org.winey.server.controller.response.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCommentResponseDto {
    private Long commentId;
    private String author;
    private String content;
    private int authorLevel;
    private LocalDateTime createdAt;

    public static GetCommentResponseDto of(Long commentId,String author, String content, int authorLevel, LocalDateTime createdAt) {
        return new GetCommentResponseDto(commentId, author, content, authorLevel, createdAt);
    }
}

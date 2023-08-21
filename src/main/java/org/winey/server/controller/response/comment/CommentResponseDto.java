package org.winey.server.controller.response.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseDto {
    private Long commentId;
    private Long authorId;
    private String author;
    private String content;
    private int authorLevel;
    private LocalDateTime createdAt;

    public static CommentResponseDto of(Long commentId, Long authorId, String author, String content, int authorLevel, LocalDateTime createdAt) {
        return new CommentResponseDto(commentId, authorId, author, content, authorLevel, createdAt);
    }
}

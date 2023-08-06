package org.winey.server.controller.request.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateCommentRequestDto {
    @NotNull
    @Size(max = 500, message = "댓글은 500자 이하여야 합니다.")
    private String content;
}

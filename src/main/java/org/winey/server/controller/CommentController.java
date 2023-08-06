package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.config.resolver.UserId;
import org.winey.server.controller.request.comment.CreateCommentRequestDto;
import org.winey.server.controller.request.feedLike.CreateFeedLikeRequestDto;
import org.winey.server.controller.response.comment.CreateCommentResponseDto;
import org.winey.server.controller.response.feedLike.CreateFeedLikeResponseDto;
import org.winey.server.exception.Success;
import org.winey.server.service.CommentService;
import org.winey.server.service.FeedLikeService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "Comment 댓글", description = "위니 피드 댓글 API Document")
public class CommentController {
    private final CommentService commentService;

    @PostMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "댓글을 만드는 API", description = "피드의 댓글을 생성합니다.")
    public ApiResponse<CreateCommentResponseDto> createComment(
            @UserId Long userId,
            @PathVariable Long feedId,
            @RequestBody @Valid CreateCommentRequestDto requestDto
    ) {
        return ApiResponse.success(Success.CREATE_COMMENT_RESPONSE_SUCCESS, commentService.createComment(userId, feedId, requestDto.getContent()));
    }

    @DeleteMapping(value = "/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "댓글 삭제 API", description = "피드의 댓글을 삭제합니다.")
    public ApiResponse deleteComment(
            @UserId Long userId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userId, commentId);
        return ApiResponse.success(Success.DELETE_COMMENT_SUCCESS);
    }

}

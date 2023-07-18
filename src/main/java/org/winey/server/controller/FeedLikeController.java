package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.feedLike.CreateFeedLikeRequestDto;
import org.winey.server.controller.response.feedLike.CreateFeedLikeResponseDto;
import org.winey.server.exception.Success;
import org.winey.server.service.FeedLikeService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedLike")
@Tag(name = "feedLike 좋아요", description = "위니 피드 좋아요 API Document")
public class FeedLikeController {
    private final FeedLikeService feedLikeService;

    @PostMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "좋아요를 만드는 API", description = "피드의 좋아요를 생성합니다.")
    public ApiResponse<CreateFeedLikeResponseDto> createFeedLike(
            @RequestHeader Long userId,
            @PathVariable Long feedId,
            @RequestBody @Valid CreateFeedLikeRequestDto requestDto
    ) {
        return ApiResponse.success(Success.CREATE_FEED_RESPONSE_SUCCESS, feedLikeService.createFeedLike(userId, feedId, requestDto.getFeedLike()));
    }

}

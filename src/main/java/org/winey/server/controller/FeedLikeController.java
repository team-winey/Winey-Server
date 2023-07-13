package org.winey.server.controller;

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
public class FeedLikeController {
    private final FeedLikeService feedLikeService;

    @PostMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<CreateFeedLikeResponseDto> createFeedLike(
            @RequestHeader Long userId,
            @PathVariable Long feedId,
            @RequestBody @Valid CreateFeedLikeRequestDto requestDto
            ) {
        return ApiResponse.success(Success.CREATE_FEED_RESPONSE_SUCCESS, feedLikeService.createFeedLike(userId, feedId, requestDto.getFeedLike()));
    }

}

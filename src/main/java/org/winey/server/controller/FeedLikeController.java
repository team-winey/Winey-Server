package org.winey.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.service.FeedLikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedLike")
public class FeedLikeController {
    private final FeedLikeService feedLikeService;

    @PostMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse createFeedLike(
            @RequestHeader("userId") Long userId,
            @PathVariable Long feedId,
            @RequestBody boolean feedLike
    ) {


    }

}

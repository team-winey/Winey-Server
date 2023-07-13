package org.winey.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.CreateFeedRequestDto;
import org.winey.server.controller.response.feed.CreateFeedResponseDto;
import org.winey.server.controller.response.feed.GetAllFeedResponseDto;
import org.winey.server.exception.Error;
import org.winey.server.exception.Success;
import org.winey.server.external.client.aws.S3Service;
import org.winey.server.service.FeedService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final S3Service s3Service;
    private final FeedService feedService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateFeedResponseDto> createFeed(
            @RequestHeader("userId") Long userId,
            @ModelAttribute CreateFeedRequestDto request) {
        String feedImageUrl = s3Service.uploadImage(request.getFeedImage(), "feed");
        return ApiResponse.success(Success.CREATE_BOARD_SUCCESS, feedService.createFeed(request, userId, feedImageUrl));
    }

    @DeleteMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse deleteFeed(
            @RequestHeader("userId") Long userId,
            @PathVariable Long feedId
    ) {
        String imageUrl = feedService.deleteFeed(userId, feedId);
        //https://s3.ap-northeast-2.amazonaws.com/winey-s3/feed/image/178bdf26-5eb9-449f-8abf-1716c71cd3fa.png
        //feed/image/178bdf26-5eb9-449f-8abf-1716c71cd3fa.png
        s3Service.deleteFile(imageUrl);
        return ApiResponse.success(Success.DELETE_FEED_SUCCESS);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetAllFeedResponseDto> getAllFeed(@RequestParam int page, @RequestHeader Long userId) {
        if (page < 1) return ApiResponse.error(Error.PAGE_REQUEST_VALIDATION_EXCEPTION, Error.PAGE_REQUEST_VALIDATION_EXCEPTION.getMessage());
        return ApiResponse.success(Success.GET_FEED_LIST_SUCCESS, feedService.getAllFeed(page, userId));
    }

    @GetMapping("/myFeed")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetAllFeedResponseDto> getMyFeed(@RequestParam int page, @RequestHeader Long userId) {
        if (page < 1) return ApiResponse.error(Error.PAGE_REQUEST_VALIDATION_EXCEPTION, Error.PAGE_REQUEST_VALIDATION_EXCEPTION.getMessage());
        return ApiResponse.success(Success.GET_MYFEED_SUCCESS, feedService.getMyFeed(page, userId));
    }
}

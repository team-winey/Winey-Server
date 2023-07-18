package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Feed", description = "위니 피드 API Document")
public class FeedController {
    private final S3Service s3Service;
    private final FeedService feedService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "위니 피드 생성 API", description = "위니 피드를 서버에 등록합니다.")
    public ApiResponse<CreateFeedResponseDto> createFeed(
            @RequestHeader("userId") Long userId,
            @ModelAttribute CreateFeedRequestDto request) {
        String feedImageUrl = s3Service.uploadImage(request.getFeedImage(), "feed");
        return ApiResponse.success(Success.CREATE_BOARD_SUCCESS, feedService.createFeed(request, userId, feedImageUrl));
    }

    @DeleteMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "위니 피드 삭제 API", description = "위니 피드를 서버에서 삭제합니다.")
    public ApiResponse deleteFeed(
            @RequestHeader("userId") Long userId,
            @PathVariable Long feedId
    ) {
        String imageUrl = feedService.deleteFeed(userId, feedId);
        s3Service.deleteFile(imageUrl);
        return ApiResponse.success(Success.DELETE_FEED_SUCCESS);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)

    @Operation(summary = "위니 피드 전체 조회 API", description = "위니 피드 전체를 조회합니다.")
    public ApiResponse<GetAllFeedResponseDto> getAllFeed(@RequestParam int page, @RequestHeader Long userId) {
        if (page < 1)
            return ApiResponse.error(Error.PAGE_REQUEST_VALIDATION_EXCEPTION, Error.PAGE_REQUEST_VALIDATION_EXCEPTION.getMessage());
        return ApiResponse.success(Success.GET_FEED_LIST_SUCCESS, feedService.getAllFeed(page, userId));
    }

    @GetMapping("/myFeed")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "마이 피드 조회 API", description = "마이 피드를 조회합니다.")
    public ApiResponse<GetAllFeedResponseDto> getMyFeed(@RequestParam int page, @RequestHeader Long userId) {
        if (page < 1)
            return ApiResponse.error(Error.PAGE_REQUEST_VALIDATION_EXCEPTION, Error.PAGE_REQUEST_VALIDATION_EXCEPTION.getMessage());
        return ApiResponse.success(Success.GET_MYFEED_SUCCESS, feedService.getMyFeed(page, userId));
    }
}

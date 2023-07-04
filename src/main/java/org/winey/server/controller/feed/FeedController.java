package org.winey.server.controller.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.feed.request.CreateFeedRequestDto;
import org.winey.server.exception.Success;
import org.winey.server.external.client.aws.S3Service;
import org.winey.server.service.FeedService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final S3Service s3Service;
    private final FeedService feedService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(
            @RequestHeader("userId") Long userId,
            @ModelAttribute @Valid final CreateFeedRequestDto request) {
        String feedImageUrl = s3Service.uploadImage(request.getFeedImage(), "feed");
        return ApiResponse.success(Success.CREATE_BOARD_SUCCESS);
    }

}

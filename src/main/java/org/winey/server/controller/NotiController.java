package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.config.resolver.UserId;
import org.winey.server.controller.response.feed.GetAllFeedResponseDto;
import org.winey.server.controller.response.notification.GetAllNotiResponseDto;
import org.winey.server.exception.Error;
import org.winey.server.exception.Success;
import org.winey.server.service.FeedService;
import org.winey.server.service.NotiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noti")
@Tag(name = "Notification", description = "위니 알림 API Document")
public class NotiController {
    private final NotiService notiService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "위니 알림 전체 조회 API", description = "위니 알림 전체를 조회합니다.")
    public ApiResponse<GetAllNotiResponseDto> getAllNoti(@UserId Long userId) {
        return ApiResponse.success(Success.GET_NOTIFICATIONS_SUCCESS, notiService.getAllNoti(userId));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "위니 안읽은 알림 전체 확인 API", description = "위니 알림 전체를 확인합니다.")
    public ApiResponse checkAllNoti(@UserId Long userId){
        notiService.checkAllNoti(userId);
        return ApiResponse.success(Success.CHECK_ALL_NOTIFICATIONS);
    }


}

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

import java.util.HashMap;
import java.util.Map;

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
        GetAllNotiResponseDto res = notiService.getAllNoti(userId);
        if (res ==null){
            return ApiResponse.success(Success.NOTIFICATION_EMPTY_SUCCESS);
        }
        return ApiResponse.success(Success.GET_NOTIFICATIONS_SUCCESS, res);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "위니 안읽은 알림 읽음처리 API", description = "위니 알림 전체를 읽음처리 합니다.")
    public ApiResponse checkAllNoti(@UserId Long userId){
        notiService.checkAllNoti(userId);
        return ApiResponse.success(Success.CHECK_ALL_NOTIFICATIONS);
    }

    @GetMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "새로운 알림 여부 체크 API", description = "미확인 알림이 있는지 확인합니다.")
    public ApiResponse checkNewNoti(@UserId Long userId) {
        Map<String, Boolean> result = new HashMap<String, Boolean>() {
            {
                put("hasNewNotification", notiService.checkNewNoti(userId));
            }
        };
        return ApiResponse.success(Success.CHECK_NEW_NOTIFICATION_SUCCESS, result);
    }

}

package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.response.user.UserResponseDto;
import org.winey.server.exception.Success;
import org.winey.server.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "유저 API Document")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "마이페이지 API", description = "유저의 마이페이지를 조회합니다.")
    public ApiResponse<UserResponseDto> getUser(@RequestHeader Long userId) {
        return ApiResponse.success(Success.GET_USER_SUCCESS, userService.getUser(userId));
    }
}

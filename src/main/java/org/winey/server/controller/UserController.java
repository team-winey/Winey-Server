package org.winey.server.controller;

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
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserResponseDto> getUser(@RequestHeader Long userId) {
        return ApiResponse.success(Success.GET_USER_SUCCESS, userService.getUser(userId));
    }
}

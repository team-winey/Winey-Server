package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.config.resolver.UserId;
import org.winey.server.controller.request.UpdateUserNicknameDto;
import org.winey.server.controller.response.user.UserResponseDto;
import org.winey.server.exception.Error;
import org.winey.server.exception.Success;
import org.winey.server.exception.model.ConflictException;
import org.winey.server.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User", description = "유저 API Document")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "마이페이지 API", description = "유저의 마이페이지를 조회합니다.")
    public ApiResponse<UserResponseDto> getUser(@UserId Long userId) {
        return ApiResponse.success(Success.GET_USER_SUCCESS, userService.getUser(userId));
    }

    @PatchMapping("/nickname")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "닉네임 변경 API", description = "유저의 닉네임을 변경합니다.")
    public ApiResponse updateNickname (@UserId Long userId, @RequestBody @Valid final UpdateUserNicknameDto requestDto) {
        if (userService.checkNicknameDuplicate(requestDto.getNickname())) {
            throw new ConflictException(Error.ALREADY_EXIST_NICKNAME_EXCEPTION, Error.ALREADY_EXIST_NICKNAME_EXCEPTION.getMessage());
        }
        userService.updateNickname(userId, requestDto);
        return ApiResponse.success(Success.UPDATE_NICKNAME_SUCCESS);
    }

    @GetMapping("/nickname/is-exist")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "닉네임 중복 확인 API", description = "닉네임 중복을 확인합니다.")
    public ApiResponse checkNicknameDuplicate(@RequestParam String nickname) {
        Map<String, Boolean> result = new HashMap<String, Boolean>() {
            {
                put("isDuplicated", userService.checkNicknameDuplicate(nickname));
            }
        };
        return ApiResponse.success(Success.CHECK_NICKNAME_DUPLICATE_SUCCESS, result);
    }
}

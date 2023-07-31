package org.winey.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.auth.SignInRequestDto;
import org.winey.server.controller.response.auth.SignInResponseDto;
import org.winey.server.exception.Success;
import org.winey.server.service.auth.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth API Document")
public class AuthController {
    private final AuthService authService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SignInResponseDto> signIn(
            @RequestHeader("Authorization") String socialAccessToken,
            @RequestBody SignInRequestDto requestDto
            )
    {

        System.out.println("여기1");
        return ApiResponse.success(Success.SIGNUP_SUCCESS, authService.signIn(socialAccessToken, requestDto));

    }
}

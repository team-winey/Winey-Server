package org.winey.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.config.resolver.UserId;
import org.winey.server.controller.request.auth.SignInRequestDto;
import org.winey.server.controller.response.auth.SignInResponseDto;
import org.winey.server.controller.response.auth.TokenResponseDto;
import org.winey.server.exception.Success;
import org.winey.server.service.auth.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth API Document")
public class AuthController {
    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SignInResponseDto> signIn(
            @RequestHeader("Authorization") String socialAccessToken,
            @RequestBody SignInRequestDto requestDto
            ) {
        return ApiResponse.success(Success.LOGIN_SUCCESS, authService.signIn(socialAccessToken, requestDto));
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenResponseDto> reissueToken(@RequestHeader String refreshToken) {
        return ApiResponse.success(Success.RE_ISSUE_TOKEN_SUCCESS, authService.issueToken(refreshToken));
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse signOut(@UserId Long userId) {
        authService.signOut(userId);
        return ApiResponse.success(Success.SIGNOUT_SUCCESS);
    }
}

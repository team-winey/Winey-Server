package org.winey.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.SocialLoginRequestDto;
import org.winey.server.exception.Success;
import org.winey.server.service.SocialService;
import org.winey.server.service.SocialServiceProvider;
import org.winey.server.service.dto.request.SocialLoginRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
public class SocialController {
    private final SocialServiceProvider socialServiceProvider;

    @PostMapping("/login")
    public ApiResponse<Long> login(@RequestHeader("code") String code, @RequestBody SocialLoginRequestDto request){
        SocialService socialService = socialServiceProvider.getSocialService(request.getSocialPlatform());
        return ApiResponse.success(Success.SOCIAL_LOGIN_SUCCESS, socialService.login(SocialLoginRequest.of(code)));
    }
}

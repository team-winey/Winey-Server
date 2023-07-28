package org.winey.server.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.config.jwt.JwtService;
import org.winey.server.controller.request.auth.SignInRequestDto;
import org.winey.server.controller.response.auth.SignInResponseDto;
import org.winey.server.domain.user.SocialType;
import org.winey.server.infrastructure.UserRepository;
import org.winey.server.service.auth.apple.AppleSignInService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AppleSignInService appleSignInService;
    private final JwtService jwtService;

    @Transactional
    public SignInResponseDto signIn(String socialAccessToken, SignInRequestDto requestDto) {
        SocialType socialType = SocialType.valueOf(requestDto.getSocialType());
        String socialId =
    }
}

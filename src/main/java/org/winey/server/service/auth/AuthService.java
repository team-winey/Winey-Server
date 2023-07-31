package org.winey.server.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.config.jwt.JwtService;
import org.winey.server.controller.request.auth.SignInRequestDto;
import org.winey.server.controller.response.auth.SignInResponseDto;
import org.winey.server.domain.user.SocialType;
import org.winey.server.domain.user.SocialUser;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.SocialUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SocialUserRepository socialUserRepository;
    private final AppleSignInService appleSignInService;
    private final KakaoSignInService kakaoSignInService;
    private final JwtService jwtService;

    @Transactional
    public SignInResponseDto signIn(String socialAccessToken, SignInRequestDto requestDto) {
        System.out.println("hi3");
        SocialType socialType = SocialType.valueOf(requestDto.getSocialType());
        String email = login(socialAccessToken,socialType);
        System.out.println(email);
        Boolean isRegistered = socialUserRepository.existsByEmailAndSocialType(email, socialType);
        System.out.println(isRegistered);
        if (!isRegistered) {
            SocialUser socialUser = SocialUser.builder()
                    .nickname(socialType+"@"+email)
                    .email(email)
                    .socialType(socialType)
                    .refreshToken("")
                    .build();
            socialUserRepository.save(socialUser);
            System.out.println("소셜 유저 회원가입 완료");
        }
        System.out.println("여기를 왜 못넘어오는걸까");
        SocialUser socialUser = socialUserRepository.findByEmailAndSocialType(email, socialType)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        System.out.println("여기를 안넘어오는건가");
        // jwt 발급 (액세스 토큰, 리프레쉬 토큰)
        String accessToken = jwtService.issuedToken(String.valueOf(socialUser.getUserId()), 360 * 60 * 1000L);
        String refreshToken = jwtService.issuedToken(String.valueOf(socialUser.getUserId()), 3 * 24 * 60 * 60 * 1000L);

        // social User의 refreshToken 업데이트
        socialUser.updateRefreshToken(refreshToken);

        //
        return SignInResponseDto.of(socialUser.getUserId(), accessToken, refreshToken, socialUser.getEmail(), socialType.toString());
    }


    private String login(String socialAccessToken,SocialType socialType) {
        String socialUserId = "";
        System.out.println("하이2");
        switch (socialType.toString()) {
            case "APPLE":
                socialUserId = appleSignInService.getAppleData(socialAccessToken);
                return socialUserId;
            case "KAKAO":
                socialUserId = kakaoSignInService.getKaKaoData(socialAccessToken);
                return socialUserId;
        }
        System.out.println(socialUserId);
        return socialUserId;
    }

}

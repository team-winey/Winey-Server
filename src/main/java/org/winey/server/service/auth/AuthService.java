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
import org.winey.server.service.auth.apple.AppleLoginService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SocialUserRepository socialUserRepository;
    private final AppleSignInService appleSignInService;
    private final JwtService jwtService;
    private final AppleLoginService appleLoginService;

    @Transactional
    public SignInResponseDto signIn(String socialAccessToken, SignInRequestDto requestDto) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SocialType socialType = SocialType.valueOf(requestDto.getSocialType());
        System.out.println(socialType);
        String result = appleLoginService.getAppleId(socialAccessToken);
        System.out.println(result);
        String email = login(socialType, socialAccessToken);
        System.out.println("이메일");
        System.out.println(email);
        Boolean isRegistered = socialUserRepository.existsByEmailAndSocialType(email, socialType);

        if (!isRegistered) {
            SocialUser socialUser = SocialUser.builder()
                    .nickname(socialType + email)
                    .email(email)
                    .socialType(socialType)
                    .build();
        }

        SocialUser socialUser = socialUserRepository.findByEmailAndSocialType(email, socialType)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // jwt 발급 (액세스 토큰, 리프레쉬 토큰)
        String accessToken = jwtService.issuedToken(String.valueOf(socialUser.getUserId()), 360 * 60 * 1000L);
        String refreshToken = jwtService.issuedToken(String.valueOf(socialUser.getUserId()), 3 * 24 * 60 * 60 * 1000L);

        // social User의 refreshToken 업데이트
        socialUser.updateRefreshToken(refreshToken);

        //
        return SignInResponseDto.of(socialUser.getUserId(), accessToken, refreshToken, socialUser.getEmail(), socialType.toString());
    }

    private String login(SocialType socialType, String socialAccessToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return appleSignInService.getAppleData(socialAccessToken);
//        return switch (socialType.toString()) {
//            case "APPLE" :
//                appleSignInService.getAppleData(socialAccessToken);
//            case "KAKAO" :
//                kakaoSignInService.getKakaoData(socialAccessToken);
//            default:
//                throw new RuntimeException();
//        };
    }
}

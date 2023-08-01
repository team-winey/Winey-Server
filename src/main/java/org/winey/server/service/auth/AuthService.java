package org.winey.server.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.config.jwt.JwtService;
import org.winey.server.controller.request.auth.SignInRequestDto;
import org.winey.server.controller.response.auth.SignInResponseDto;
import org.winey.server.controller.response.auth.TokenResponseDto;
import org.winey.server.domain.user.SocialType;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.UserRepository;
import org.winey.server.service.auth.apple.AppleSignInService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AppleSignInService appleSignInService;
    private final JwtService jwtService;

    private final Long TOKEN_EXPIRATION_TIME_ACCESS = 360 * 60 * 1000L;
    private final Long TOKEN_EXPIRATION_TIME_REFRESH = 3 * 24 * 60 * 60 * 1000L;

    @Transactional
    public SignInResponseDto signIn(String socialAccessToken, SignInRequestDto requestDto) {
        SocialType socialType = SocialType.valueOf(requestDto.getSocialType());
        String socialId = login(socialType, socialAccessToken);

        Boolean isRegistered = userRepository.existsBySocialIdAndSocialType(socialId, socialType);

        if (!isRegistered) {
            User newUser = User.builder()
                    .nickname(socialType + socialId)
                    .socialId(socialId)
                    .socialType(socialType).build();
            userRepository.save(newUser);
        }

        User user = userRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // jwt 발급 (액세스 토큰, 리프레쉬 토큰)
        String accessToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_ACCESS);
        String refreshToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_REFRESH);

        user.updateRefreshToken(refreshToken);

        return SignInResponseDto.of(user.getUserId(), accessToken, refreshToken, isRegistered);
    }

    @Transactional
    public TokenResponseDto issueToken(String refreshToken) {
        jwtService.verifyToken(refreshToken);

        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // jwt 발급 (액세스 토큰, 리프레쉬 토큰)
        String newAccessToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_ACCESS);
        String newRefreshToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_REFRESH);

        user.updateRefreshToken(newRefreshToken);

        return TokenResponseDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void signOut(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        user.updateRefreshToken(null);
    }

    private String login(SocialType socialType, String socialAccessToken) {
        if (socialType.toString() == "APPLE") {
            return appleSignInService.getAppleId(socialAccessToken);
        } else {
            return "ads";
        }
    }
}

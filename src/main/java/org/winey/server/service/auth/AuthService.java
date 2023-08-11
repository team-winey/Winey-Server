package org.winey.server.service.auth;

import com.sun.net.httpserver.Authenticator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.config.jwt.JwtService;
import org.winey.server.controller.request.auth.SignInRequestDto;
import org.winey.server.controller.response.auth.SignInResponseDto;
import org.winey.server.controller.response.auth.TokenResponseDto;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.SocialType;

import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.exception.model.UnprocessableEntityException;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.UserRepository;
import org.winey.server.service.auth.apple.AppleSignInService;
import org.winey.server.service.auth.kakao.KakaoSignInService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppleSignInService appleSignInService;
    private final KakaoSignInService kakaoSignInService;
    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final Long TOKEN_EXPIRATION_TIME_ACCESS = 3 * 60 * 1000L;
    private final Long TOKEN_EXPIRATION_TIME_REFRESH = 5 * 60 * 1000L;
    private final GoalRepository goalRepository;

    private final FeedRepository feedRepository;

    @Transactional
    public SignInResponseDto signIn(String socialAccessToken, SignInRequestDto requestDto) {
        SocialType socialType = SocialType.valueOf(requestDto.getSocialType());
        String socialId = login(socialType, socialAccessToken);
        System.out.println("여기2");

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
        }
        else if (socialType.toString() == "KAKAO") {
            System.out.println("여기1");
            return kakaoSignInService.getKaKaoId(socialAccessToken);
        }
        else{
            return "ads";
        }
    }

    @Transactional
    public void withdraw(Long userId){
        User user = userRepository.findByUserId(userId).orElse(null);
        System.out.println(userId);
        if (user == null) {
            throw new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage());
        }
        System.out.println("User: " + user);
        System.out.println("Goals: " + user.getGoals());
        System.out.println("Recommends: " + user.getRecommends());
        System.out.println("Feeds: " + user.getFeeds());
        System.out.println("FeedLikes: " + user.getFeedLikes());
        System.out.println("Comments: "+ user.getComments());
        Long res = userRepository.deleteByUserId(userId); //res가 삭제된 컬럼의 개수 즉, 1이 아니면 뭔가 알 수 없는 에러.
        System.out.println(res + "개의 컬럼이 삭제되었습니다.");
        if (res!=1){
            throw new UnprocessableEntityException(Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION, Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION.getMessage());
        }
    }
}

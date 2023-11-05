package org.winey.server.controller.response.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignInResponseDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String fcmToken;
    private Boolean isRegistered;

    public static SignInResponseDto of(Long userId, String accessToken, String refreshToken, String fcmToken, Boolean isRegistered) {
        return new SignInResponseDto(userId, accessToken, refreshToken, fcmToken, isRegistered);
    }
}

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
    private String refreshToken;
    private String accessToken;
    private String email;
    private String socialType;

    public static SignInResponseDto of(Long userId, String refreshToken, String accessToken, String email, String socialType) {
        return new SignInResponseDto(userId, refreshToken, accessToken, email, socialType);
    }
}

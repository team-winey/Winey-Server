package org.winey.server.controller.response.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseUserDto {
    private Long userId;
    private String nickname;
    private String userLevel;
    private Boolean fcmIsAllowed;

    public static UserResponseUserDto of(Long userId, String nickname, String userLevel, Boolean fcmIsAllowed) {
        return new UserResponseUserDto(userId, nickname, userLevel, fcmIsAllowed);
    }
}
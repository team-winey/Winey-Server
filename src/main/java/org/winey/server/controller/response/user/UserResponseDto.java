package org.winey.server.controller.response.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {

    private Long userId;
    private String nickname;
    private String userLevel;
    private Boolean fcmIsAllowed;
    private Long savedAmount;
    private Long spentAmount;
    private Long accumulatedAmount;
    private Long accumulatedCount;

    public static UserResponseDto of(Long userId, String nickname, String userLevel,
        Boolean fcmIsAllowed, Long savedAmount, Long spentAmount, Long accumulatedAmount,
        Long accumulatedCount) {
        return new UserResponseDto(userId, nickname, userLevel, fcmIsAllowed, savedAmount,
            spentAmount, accumulatedAmount, accumulatedCount);
    }
}

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
    private Long accumulatedAmount;
    private Long amountSavedHundredDays;
    private Long amountSavedTwoWeeks;
    private Long amountSpentTwoWeeks;

    public static UserResponseDto of(Long userId, String nickname, String userLevel,
        Boolean fcmIsAllowed, Long accumulatedAmount, Long amountSavedHundredDays, Long amountSavedTwoWeeks,
        Long amountSpentTwoWeeks) {
        return new UserResponseDto(userId, nickname, userLevel, fcmIsAllowed, accumulatedAmount,
            amountSavedHundredDays, amountSavedTwoWeeks, amountSpentTwoWeeks);
    }
}

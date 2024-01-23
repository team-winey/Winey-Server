package org.winey.server.controller.response.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.user.UserLevel;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAchievementStatusResponseDto {

    private String userLevel;
    private Long remainingAmount;
    private Long remainingCount;

    public static GetAchievementStatusResponseDto of(UserLevel userLevel, Long remainingAmount, Long remainingCount) {
        return new GetAchievementStatusResponseDto(userLevel.getName(), remainingAmount, remainingCount);
    }
}

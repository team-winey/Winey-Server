package org.winey.server.controller.response.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {
    private UserDto userDto;
    private GoalDto goalDto;

    public static UserResponseDto of(UserDto userDto, GoalDto goalDto) {
        return new UserResponseDto(userDto, goalDto);
    }
}

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class UserDto {
    private Long userId;
    private String nickname;
    private String userLevel;

    public static UserDto of(Long userId, String nickname, String userLevel) {
        return new UserDto(userId, nickname, userLevel);
    }
}

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class GoalDto {
    private Long duringGoalAmount;
    private Long duringGoalCount;
    private Long targetMoney;
    private int targetDay;
    private boolean isOver;
    private boolean isAttained;

    public static GoalDto of(Long duringGoalAmount, Long duringGoalCount, Long targetMoney, int targetDay, boolean isOver, boolean isAttained) {
        return new GoalDto(duringGoalAmount, duringGoalCount, targetMoney, targetDay, isOver, isAttained);
    }

}

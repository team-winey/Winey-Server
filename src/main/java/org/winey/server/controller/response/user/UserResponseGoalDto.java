package org.winey.server.controller.response.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.goal.GoalType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseGoalDto {
    private GoalType goalType;
    private Long duringGoalAmount;
    private Long duringGoalCount;
    private Boolean isAttained;

    public static UserResponseGoalDto of(
        GoalType goalType,
        Long duringGoalAmount,
        Long duringGoalCount,
        boolean isAttained)
    {
        return new UserResponseGoalDto(
            goalType,
            duringGoalAmount,
            duringGoalCount,
            isAttained
        );
    }
}

package org.winey.server.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;

@Getter
@AllArgsConstructor
public enum GoalType {
    COMMONER_GOAL(UserLevel.COMMONER, 5000, "아메리카노"),
    KNIGHT_GOAL(UserLevel.KNIGHT, 30000, "치킨"),
    ARISTOCRAT_GOAL(UserLevel.ARISTOCRAT, 150000, "운동화"),
    EMPEROR_GOAL(UserLevel.ARISTOCRAT, 300000, "에어팟");

    private final UserLevel userLevel;
    private final int targetMoney;
    private final String targetProduct;

    public GoalType getGoalTypeByUserLevel(UserLevel userLevel) {
        for (GoalType goalType : GoalType.values()) {
            if (goalType.getUserLevel() == userLevel) {
                return goalType;
            }
        }
        throw new BadRequestException(Error.INVALID_USER_LEVEL_EXCEPTION,
            Error.INVALID_USER_LEVEL_EXCEPTION.getMessage());
    }
}

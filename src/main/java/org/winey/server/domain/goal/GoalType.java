package org.winey.server.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.winey.server.domain.user.UserLevel;

@Getter
@AllArgsConstructor
public enum GoalType {
    COMMONER_GOAL(UserLevel.COMMONER, 5000, 2, "아메리카노"),
    KNIGHT_GOAL(UserLevel.KNIGHT, 30000, 7,  "치킨"),
    ARISTOCRAT_GOAL(UserLevel.ARISTOCRAT, 150000, 10,  "운동화"),
    EMPEROR_GOAL(UserLevel.EMPEROR, 300000, 20, "에어팟");

    private final UserLevel userLevel;
    private final int targetMoney;
    private final int targetCount;
    private final String targetProduct;

    public static GoalType findGoalTypeByUserLevel(UserLevel userLevel) {
        for (GoalType goalType : GoalType.values()) {
            if (goalType.getUserLevel() == userLevel) {
                return goalType;
            }
        }
        return null;
    }
}

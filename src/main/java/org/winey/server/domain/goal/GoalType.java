package org.winey.server.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.winey.server.domain.user.UserLevel;

@Getter
@AllArgsConstructor
public enum GoalType {
    COMMONER_GOAL(UserLevel.COMMONER, 5000, "아메리카노"),
    KNIGHT_GOAL(UserLevel.KNIGHT, 30000, "치킨"),
    ARISTOCRAT_GOAL(UserLevel.ARISTOCRAT, 150000, "운동화"),
    EMPEROR_GOAL(UserLevel.EMPEROR, 300000, "에어팟");

    private final UserLevel userLevel;
    private final int targetMoney;
    private final String targetProduct;
}

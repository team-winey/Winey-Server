package org.winey.server.controller.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseGoalDto {
    private Long duringGoalAmount;
    private Long duringGoalCount;
    private Long targetMoney;
    private int targetDay;
    private int dDay;
    private Boolean isOver;
    private Boolean isAttained;

    public static UserResponseGoalDto of(Long duringGoalAmount, Long duringGoalCount, Long targetMoney, int targetDay, int dDay, boolean isOver, boolean isAttained) {
        return new UserResponseGoalDto(duringGoalAmount, duringGoalCount, targetMoney, targetDay, dDay, isOver, isAttained);
    }

}

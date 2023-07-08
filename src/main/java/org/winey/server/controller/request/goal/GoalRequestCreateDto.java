package org.winey.server.controller.request.goal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRequestCreateDto {
    @DecimalMin(value = "30000")
    private Long targetMoney;
    @DecimalMin(value = "5")
    private int targetDay;
}

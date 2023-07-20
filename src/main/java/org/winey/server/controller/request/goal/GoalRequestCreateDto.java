package org.winey.server.controller.request.goal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRequestCreateDto {
    @DecimalMin(value = "30000") @DecimalMax(value = "999999999")
    private Long targetMoney;
    @DecimalMin(value = "5") @DecimalMax(value = "365")
    private int targetDay;
}

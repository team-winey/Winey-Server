package org.winey.server.controller.response.goal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalResponseCreateDto {
    private Long userId;
    private Long targetMoney;
    private LocalDate targetDate;
    private LocalDateTime createdAt;

    public static GoalResponseCreateDto of(Long userId, Long targetMoney, LocalDate targetDate, LocalDateTime createdAt) {
        return new GoalResponseCreateDto(userId, targetMoney, targetDate, createdAt);
    }
}

package org.winey.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserLevel {
    COMMONER("평민", 1, 0L, 0L),
    KNIGHT("기사", 2, 30000L, 2L),
    ARISTOCRAT("귀족", 3, 150000L, 4L),
    EMPEROR("황제", 4, 300000L, 6L);

    private final String name;
    private final int levelNumber;
    private final Long minimumAmount;
    private final Long minimumCount;

    public static UserLevel calculateUserLevel(Long amount, Long count) {
        if (amount >= EMPEROR.minimumAmount && count >= EMPEROR.minimumCount) {
            return EMPEROR;
        } else if (amount >= ARISTOCRAT.minimumAmount && count >= ARISTOCRAT.minimumCount) {
            return ARISTOCRAT;
        } else if (amount >= KNIGHT.minimumAmount && count >= KNIGHT.minimumCount) {
            return KNIGHT;
        }
        return COMMONER;
    }

    public static UserLevel getNextUserLevel(UserLevel currentLevel) {
        for (UserLevel level : UserLevel.values()) {
            if (level.getLevelNumber() == currentLevel.getLevelNumber() + 1) {
                return level;
            }
        }
        return null;
    }
}

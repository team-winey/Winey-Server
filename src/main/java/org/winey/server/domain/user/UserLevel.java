package org.winey.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserLevel {
    COMMONER("평민", 1),
    KNIGHT("기사", 2),
    ARISTOCRAT("귀족", 3),
    EMPEROR("황제", 4);

    private final String name;
    private final int levelNumber;
}

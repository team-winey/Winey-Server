package org.winey.server.controller.response.recommend;

import lombok.*;

@Getter
public class RecommendResponseUserDto {
    private final Long userId;
    private final String nickname;

    @Builder
    public RecommendResponseUserDto(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }
}

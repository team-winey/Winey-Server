package org.winey.server.controller.response.recommend;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendResponseUserDto {
    private Long userId;
    private String nickname;

    public static RecommendResponseUserDto of(Long userId, String nickname) {
        return new RecommendResponseUserDto(userId, nickname);
    }
}

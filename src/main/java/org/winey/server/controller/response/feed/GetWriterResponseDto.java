package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWriterResponseDto {
    private Long userId;
    private String nickName;
    private int userLevel;

    public static GetWriterResponseDto of(Long userId, String nickName, int userLevel){
        return new GetWriterResponseDto(userId, nickName, userLevel);
    }

}

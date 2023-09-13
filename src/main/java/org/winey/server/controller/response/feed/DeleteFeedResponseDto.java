package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteFeedResponseDto {
    Long feedId;

    public static DeleteFeedResponseDto of(Long feedId){
        return new DeleteFeedResponseDto(feedId);
    }
}

package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateFeedResponseDto {
    private Long feedId;
    private LocalDateTime createdAt;
    private Boolean levelUpgraded;

    public static CreateFeedResponseDto of(Long feedId, LocalDateTime createdAt, Boolean levelUpgraded){
        return new CreateFeedResponseDto(feedId, createdAt, levelUpgraded);
    }

}

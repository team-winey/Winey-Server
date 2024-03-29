package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetFeedResponseDto {
    private Long feedId;
    // user부분
    private Long userId;
    private String nickName;
    private int writerLevel;
    // 여기까쥐
    private String feedType;
    private String feedTitle;
    private String feedImage;
    private Long feedMoney;
    private Boolean isLiked;
    private Long likes;
    private Long comments;
    private String timeAgo;
    private LocalDateTime createdAt;


    public static GetFeedResponseDto of(Long feedId, Long userId, String nickName, int writerLevel, String feedType, String feedTitle, String feedImage,
                                        Long feedMoney, Boolean isLiked, Long likes, Long comments, String timeAgo, LocalDateTime createdAt) {
        return new GetFeedResponseDto(feedId, userId, nickName, writerLevel, feedType, feedTitle, feedImage, feedMoney, isLiked, likes, comments, timeAgo, createdAt);
    }
}

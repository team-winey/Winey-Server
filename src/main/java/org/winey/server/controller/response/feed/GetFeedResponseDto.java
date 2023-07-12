package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;

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
    private String feedTitle;
    private String feedImage;
    private Long feedMoney;
    private Boolean isLiked;
    private int likes;
    private LocalDate createdAt;


    public static GetFeedResponseDto of(Long feedId,Long userId, String nickName, int writerLevel,String feedTitle, String feedImage,
                                 Long feedMoney, Boolean isLiked, int likes, LocalDate createdAt){
        return new GetFeedResponseDto(feedId,userId,nickName,writerLevel, feedTitle, feedImage, feedMoney, isLiked, likes, createdAt);
    }
}

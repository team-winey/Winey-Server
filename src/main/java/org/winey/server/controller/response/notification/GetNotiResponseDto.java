package org.winey.server.controller.response.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.controller.response.feed.GetFeedResponseDto;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetNotiResponseDto {
    private Long notiId;
    // user부분
    private String notiReceiver; //알림 받은 유저 닉네임.
    private String notiMessage;
    private NotiType notiType;
    private Boolean isChecked; //유저가 이 알림을 체크했는지
    private Long LinkId; //좋아요, 댓글일 경우에는 feedid를 넘기고 아니면 안넘어감.
    private LocalDateTime createdAt;

    public static GetNotiResponseDto of(Long notiId,String notiReceiver, String notiMessage, NotiType notiType, boolean isChecked, Long linkId, LocalDateTime createdAt){
        return new GetNotiResponseDto(notiId,notiReceiver,notiMessage,notiType,isChecked,linkId,createdAt);
    }
}

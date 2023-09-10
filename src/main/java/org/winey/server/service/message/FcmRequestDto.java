package org.winey.server.service.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmRequestDto implements Serializable {
    private String message;
    private String fcmToken;

    private NotiType notiType;

    public static FcmRequestDto of(String message, String fcmToken, NotiType notiType){
        return new FcmRequestDto(message,fcmToken,notiType);
    }
}

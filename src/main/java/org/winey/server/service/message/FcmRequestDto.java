package org.winey.server.service.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;

import java.io.Serializable;

import javax.annotation.Nullable;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmRequestDto implements Serializable {
    private String message;
    private String token; //fcm
    private NotiType type;
    private Long feedId;

    public static FcmRequestDto of(String message, String token, NotiType type, Long feedId){
        return new FcmRequestDto(message,token,type,feedId);
    }
}

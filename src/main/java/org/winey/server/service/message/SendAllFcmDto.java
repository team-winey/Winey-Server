package org.winey.server.service.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.notification.NotiType;

import java.io.Serializable;
import java.util.List;
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendAllFcmDto {
    private List<String> tokenList;

    private String title;

    private String message;

    public static SendAllFcmDto of(List<String> tokenList, String title, String message){
        return new SendAllFcmDto(tokenList, title, message);
    }
}

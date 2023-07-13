package org.winey.server.controller.request;

import lombok.*;
import org.winey.server.domain.SocialPlatform;
import org.winey.server.service.dto.request.SocialLoginRequest;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialLoginRequestDto {
    private SocialPlatform socialPlatform;
    public static SocialLoginRequestDto of(SocialPlatform socialPlatform) {
        return new SocialLoginRequestDto(socialPlatform);
    }
}

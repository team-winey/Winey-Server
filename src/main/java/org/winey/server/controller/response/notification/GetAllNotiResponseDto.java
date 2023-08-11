package org.winey.server.controller.response.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.feed.GetAllFeedResponseDto;
import org.winey.server.controller.response.feed.GetFeedResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAllNotiResponseDto {
    private List<GetNotiResponseDto> getNotiResponseDtoList;

    public static GetAllNotiResponseDto of(List<GetNotiResponseDto> getNotiResponseDtoList) {
        return new GetAllNotiResponseDto(getNotiResponseDtoList);
    }

}

package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.recommend.RecommendListResponseDto;
import org.winey.server.controller.response.recommend.RecommendResponseDto;
import org.winey.server.controller.response.recommend.RecommendResponseUserDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetAllFeedResponseDto {
    private PageResponseDto pageResponseDto;
    private List<GetFeedResponseDto> getFeedResponseDtoList;

    public static GetAllFeedResponseDto of(PageResponseDto pageResponseDto, List<GetFeedResponseDto> getFeedResponseDtoList) {
        return new GetAllFeedResponseDto(pageResponseDto,getFeedResponseDtoList);
    }

}

package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.comment.CreateCommentResponseDto;
import org.winey.server.controller.response.comment.GetCommentResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetFeedDetailResponseDto {

    private GetFeedResponseDto getFeedResponseDto;
    private List<GetCommentResponseDto> getCommentResponseList;

    public static GetFeedDetailResponseDto of(GetFeedResponseDto getFeedResponseDto, List<GetCommentResponseDto> getCommentResponseList) {
        return new GetFeedDetailResponseDto(getFeedResponseDto,getCommentResponseList);
    }
}


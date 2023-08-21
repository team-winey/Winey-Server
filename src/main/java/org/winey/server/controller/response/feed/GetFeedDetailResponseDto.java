package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.controller.response.comment.CommentResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetFeedDetailResponseDto {

    private GetFeedResponseDto getFeedResponseDto;
    private List<CommentResponseDto> getCommentResponseList;

    public static GetFeedDetailResponseDto of(GetFeedResponseDto getFeedResponseDto, List<CommentResponseDto> getCommentResponseList) {
        return new GetFeedDetailResponseDto(getFeedResponseDto,getCommentResponseList);
    }
}


package org.winey.server.controller.response.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportFeedResponseDto {
    private Long reporterId;
    private Long reportFeedId;

    private String reportMessage;

    public static ReportFeedResponseDto of(Long reporterId, Long reportFeedId, String reportMessage){
        return new ReportFeedResponseDto(reporterId, reportFeedId, reportMessage);
    }

}
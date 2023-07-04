package org.winey.server.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResponseDto {
    private int totalPageSize;
    private int currentPageIndex;
    private boolean isEnd;

    public static PageResponseDto of(int totalPageSize, int currentPageIndex, boolean isEnd) {
        return new PageResponseDto(totalPageSize, currentPageIndex, isEnd);
    }
}

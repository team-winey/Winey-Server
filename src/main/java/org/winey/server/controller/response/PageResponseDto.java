package org.winey.server.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResponseDto {
    private final int totalPageSize;
    private final int currentPageIndex;
    private final boolean isEnd;

    @Builder
    public PageResponseDto(int totalPageSize, int currentPageIndex, boolean isEnd) {
        this.totalPageSize = totalPageSize;
        this.currentPageIndex = currentPageIndex;
        this.isEnd = isEnd;
    }
}

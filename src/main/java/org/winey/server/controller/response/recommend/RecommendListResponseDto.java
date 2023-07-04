package org.winey.server.controller.response.recommend;

import lombok.Builder;
import lombok.Getter;
import org.winey.server.controller.response.PageResponseDto;

import java.util.List;

@Getter
public class RecommendListResponseDto {
    private final RecommendResponseUserDto recommendResponseUserDto;
    private final PageResponseDto pageResponseDto;
    private final List<RecommendResponseDto> recommendsResponseDto;

    @Builder
    public RecommendListResponseDto(RecommendResponseUserDto recommendResponseUserDto, PageResponseDto pageResponseDto, List<RecommendResponseDto> recommendsResponseDto) {
        this.recommendResponseUserDto = recommendResponseUserDto;
        this.pageResponseDto = pageResponseDto;
        this.recommendsResponseDto = recommendsResponseDto;
    }
}

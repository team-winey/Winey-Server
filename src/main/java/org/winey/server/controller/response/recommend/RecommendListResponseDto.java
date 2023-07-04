package org.winey.server.controller.response.recommend;

import lombok.*;
import org.winey.server.controller.response.PageResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendListResponseDto {
    private RecommendResponseUserDto recommendResponseUserDto;
    private PageResponseDto pageResponseDto;
    private List<RecommendResponseDto> recommendsResponseDto;

    public static RecommendListResponseDto of(RecommendResponseUserDto recommendResponseUserDto, PageResponseDto pageResponseDto, List<RecommendResponseDto> recommendsResponseDto) {
        return new RecommendListResponseDto(recommendResponseUserDto, pageResponseDto, recommendsResponseDto);
    }
}

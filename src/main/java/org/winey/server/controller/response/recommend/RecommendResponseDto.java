package org.winey.server.controller.response.recommend;

import lombok.*;

import java.time.LocalDateTime;

@Getter
public class RecommendResponseDto {
    private final Long recommendId;
    @Setter
    private String recommendLink;
    private final String recommendTitle;
    @Setter
    private Long recommendWon;
    @Setter
    private Long recommendPercent;
    private final String recommendImage;
    private final LocalDateTime createdAt;

    @Builder
    public RecommendResponseDto(Long recommendId, String recommendTitle, String recommendImage, LocalDateTime createdAt) {
        this.recommendId = recommendId;
        this.recommendTitle = recommendTitle;
        this.recommendImage = recommendImage;
        this.createdAt = createdAt;
    }
}

package org.winey.server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.recommend.RecommendListResponseDto;
import org.winey.server.controller.response.recommend.RecommendResponseDto;
import org.winey.server.domain.recommend.Recommend;
import org.winey.server.infrastructure.RecommendRepository;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final DecimalFormat decFormat = new DecimalFormat("###,###");

    @Transactional(readOnly = true)
    public RecommendListResponseDto getRecommend(int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 20);
        Page<Recommend> recommendPage = recommendRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        PageResponseDto pageInfo = PageResponseDto.of(recommendPage.getTotalPages(), recommendPage.getNumber() + 1, (recommendPage.getTotalPages() == recommendPage.getNumber() + 1));

        List<RecommendResponseDto> recommendInfos = recommendPage.stream()
                .map(recommend -> RecommendResponseDto.of(
                        recommend.getRecommendId(),
                        recommend.getRecommendLink(),
                        recommend.getRecommendTitle(),
                        recommend.getRecommendSubTitle(),
                        recommend.getRecommendPercent() != null ? String.valueOf(recommend.getRecommendPercent()) + "%" : String.valueOf(decFormat.format(recommend.getRecommendWon()) + "원"),
                        recommend.getRecommendImage(),
                        recommend.getCreatedAt()
                )).collect(Collectors.toList());

        return RecommendListResponseDto.of(pageInfo, recommendInfos);
    }
}

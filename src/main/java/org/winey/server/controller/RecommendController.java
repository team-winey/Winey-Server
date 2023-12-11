package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.response.recommend.RecommendListResponseDto;
import org.winey.server.exception.Error;
import org.winey.server.exception.Success;
import org.winey.server.service.RecommendService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
@Tag(name = "Recommend", description = "추천 위니 API Document")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "추천 위니 조회 API", description = "추천 위니를 조회합니다.")
    public ApiResponse<RecommendListResponseDto> getRecommend(@RequestParam int page) {
        if (page < 1)
            return ApiResponse.error(Error.PAGE_REQUEST_VALIDATION_EXCEPTION, Error.PAGE_REQUEST_VALIDATION_EXCEPTION.getMessage());
        return ApiResponse.success(Success.GET_RECOMMEND_LIST_SUCCESS, recommendService.getRecommend(page));
    }
}

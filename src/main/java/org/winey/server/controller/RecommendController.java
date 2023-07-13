package org.winey.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.response.recommend.RecommendListResponseDto;
import org.winey.server.exception.Error;
import org.winey.server.exception.Success;
import org.winey.server.service.RecommendService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RecommendListResponseDto> getRecommend(@RequestParam int page) {
        if (page < 1) return ApiResponse.error(Error.PAGE_REQUEST_VALIDATION_EXCEPTION, Error.PAGE_REQUEST_VALIDATION_EXCEPTION.getMessage());
        return ApiResponse.success(Success.GET_RECOMMEND_LIST_SUCCESS, recommendService.getRecommend(page));
    }


}

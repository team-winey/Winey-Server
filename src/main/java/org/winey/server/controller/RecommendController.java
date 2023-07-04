package org.winey.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.response.recommend.RecommendListResponseDto;
import org.winey.server.service.RecommendService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RecommendListResponseDto> getRecommend(@RequestParam final int page) {

    }


    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RetrospectResponseDto> getOne(@RequestParam final String date){
        RetrospectResponseDto data = retrospectService.getOne(date);
        if (data == null) {
            return ApiResponse.success(Success.GET_RETROSPECT_NO_CONTENT_SUCCESS);
        } else {
            return ApiResponse.success(Success.GET_RETROSPECT_SUCCESS, data);
        }
    }

}

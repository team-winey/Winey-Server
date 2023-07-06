package org.winey.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.goal.GoalRequestCreateDto;
import org.winey.server.controller.response.goal.GoalResponseCreateDto;
import org.winey.server.exception.Success;
import org.winey.server.service.GoalService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<GoalResponseCreateDto> create(@RequestBody final GoalRequestCreateDto requestDto, @RequestHeader Long userId) {
        return ApiResponse.success(Success.CREATE_GOAL_SUCCESS, goalService.createGoal(requestDto, userId));
    }
}

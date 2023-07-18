package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.goal.GoalRequestCreateDto;
import org.winey.server.controller.response.goal.GoalResponseCreateDto;
import org.winey.server.exception.Success;
import org.winey.server.service.GoalService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
@Tag(name = "Goal", description = "위니 목표 API Document")
public class GoalController {

    private final GoalService goalService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "목표 생성 API", description = "위니 목표를 설정합니다.")
    public ApiResponse<GoalResponseCreateDto> create(@RequestBody @Valid final GoalRequestCreateDto requestDto, @RequestHeader Long userId) {
        return ApiResponse.success(Success.CREATE_GOAL_SUCCESS, goalService.createGoal(requestDto, userId));
    }
}

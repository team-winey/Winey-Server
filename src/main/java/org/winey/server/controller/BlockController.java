package org.winey.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.config.resolver.UserId;
import org.winey.server.exception.Success;
import org.winey.server.service.BlockService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block")
@Tag(name = "Block", description = "차단 API Document")
public class BlockController {

    private final BlockService blockService;

    @PostMapping(value = "/{feedId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "유저 차단 API", description = "해당 피드를 작성한 유저를 차단합니다.")
    public ApiResponse createBlockUser(
        @UserId Long userId,
        @PathVariable Long feedId
    ) {
        blockService.createBlockUser(feedId, userId);
        return ApiResponse.success(Success.BLOCK_USER_SUCCESS);
    }
}

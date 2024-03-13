package org.winey.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.broadcast.BroadCastAllUserDto;
import org.winey.server.exception.Success;
import org.winey.server.service.BroadCastService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/broadcast")
@Tag(name = "BroadCast", description = "위니 전체 푸시 API Document")
public class BroadCastController {
	private final BroadCastService broadCastService;

	@PostMapping("/send-all")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "전체 유저에게 메시지 발송 API", description = "전체 유저에게 메시지를 발송합니다.")
	public ApiResponse sendMessageToEntireUser(@RequestBody BroadCastAllUserDto broadCastAllUserDto){
		return ApiResponse.success(Success.SEND_ENTIRE_MESSAGE_SUCCESS, broadCastService.broadAllUser(broadCastAllUserDto));
	}

}

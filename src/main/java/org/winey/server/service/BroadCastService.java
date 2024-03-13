package org.winey.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.winey.server.common.dto.ApiResponse;
import org.winey.server.controller.request.broadcast.BroadCastAllUserDto;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.Success;
import org.winey.server.exception.model.CustomException;
import org.winey.server.infrastructure.UserRepository;
import org.winey.server.service.message.SendAllFcmDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.sun.net.httpserver.Authenticator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BroadCastService {

	private final FcmService fcmService;

	private final UserRepository userRepository;

	public ApiResponse broadAllUser(BroadCastAllUserDto broadCastAllUserDto){
		List<User> allUser = userRepository.findByFcmTokenNotNull();
		List<String> tokenList;
		if (!allUser.isEmpty()){
			try {
				tokenList = allUser.stream().map(
					User::getFcmToken).collect(Collectors.toList());
				System.out.println(tokenList);
				fcmService.sendAllByTokenList(
					SendAllFcmDto.of(tokenList, broadCastAllUserDto.getTitle(), broadCastAllUserDto.getMessage()));
				return ApiResponse.success(Success.SEND_ENTIRE_MESSAGE_SUCCESS,
					Success.SEND_ENTIRE_MESSAGE_SUCCESS.getMessage());
			}catch (FirebaseMessagingException | JsonProcessingException e){
				return ApiResponse.error(Error.UNPROCESSABLE_SEND_TO_FIREBASE, Error.UNPROCESSABLE_SEND_TO_FIREBASE.getMessage());
			}
		}
		return ApiResponse.error(Error.UNPROCESSABLE_FIND_USERS, Error.UNPROCESSABLE_FIND_USERS.getMessage());
	}




}

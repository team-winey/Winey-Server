package org.winey.server.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.UpdateFcmTokenDto;
import org.winey.server.controller.request.UpdateUserNicknameDto;
import org.winey.server.controller.response.user.UserResponseDto;
import org.winey.server.controller.response.user.UserResponseGoalDto;
import org.winey.server.controller.response.user.UserResponseUserDto;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.goal.GoalType;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public UserResponseDto getUser(Long userId) {
        // 1. 유저 정보를 조회한다.
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
                Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // 2. 유저의 최신 목표를 조회한다. 이때 최신 목표가 없다면 레벨에 맞는 목표를 설정한다.
        List<Goal> goalList = goalRepository.findByUserOrderByCreatedAtDesc(user);
        Goal presentGoal = goalList.stream().findFirst()
            .orElse(goalRepository.save(Goal.builder()
                .goalType(GoalType.findGoalTypeByUserLevel(user.getUserLevel()))
                .user(user)
                .build()));

        // 3. 유저와 최신 목표 정보를 반환한다.
        UserResponseUserDto userDto = UserResponseUserDto.of(user.getUserId(), user.getNickname(),
            user.getUserLevel().getName(), user.getFcmIsAllowed());
        UserResponseGoalDto goalDto = UserResponseGoalDto.of(presentGoal.getGoalType(),
            presentGoal.getDuringGoalAmount(), presentGoal.getDuringGoalCount(),
            presentGoal.isAttained());
        return UserResponseDto.of(userDto, goalDto);
    }

    @Transactional
    public void updateNickname(Long userId, UpdateUserNicknameDto requestDto) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
                Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        user.updateNickname(requestDto.getNickname());
    }

    @Transactional
    public void updateFcmToken(Long userId, UpdateFcmTokenDto updateFcmTokenDto) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
                Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        user.updateFcmToken(updateFcmTokenDto.getToken());
    }

    //푸시알림 동의 여부 수정 api
    @Transactional
    public Boolean allowedPushNotification(Long userId, Boolean fcmIsAllowed) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
                Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        if (fcmIsAllowed == user.getFcmIsAllowed()) {   //같은 경우면 에러가 날 수 있으니 에러 띄움.
            throw new BadRequestException(Error.REQUEST_VALIDATION_EXCEPTION,
                Error.REQUEST_VALIDATION_EXCEPTION.getMessage());
        }
        user.updateFcmIsAllowed(fcmIsAllowed);
        return fcmIsAllowed;
    }

    public Boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}

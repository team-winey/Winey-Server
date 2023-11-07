package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.UpdateFcmTokenDto;
import org.winey.server.controller.request.UpdateUserNicknameDto;
import org.winey.server.controller.response.user.UserResponseDto;
import org.winey.server.controller.response.user.UserResponseGoalDto;
import org.winey.server.controller.response.user.UserResponseUserDto;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        UserResponseUserDto userDto = UserResponseUserDto.of(user.getUserId(), user.getNickname(), user.getUserLevel().getName());

        List<Goal> goalList = goalRepository.findByUserOrderByCreatedAtDesc(user);

        if (goalList.size() == 0) {
            return UserResponseDto.of(userDto, null);
        }
        Goal presentGoal = goalList.stream().findFirst()
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_GOAL_EXCEPTION, Error.NOT_FOUND_GOAL_EXCEPTION.getMessage()));

        int targetDay = (int) Period.between(presentGoal.getCreatedAt().toLocalDate(), presentGoal.getTargetDate()).getDays();
        int dDay = (int) ChronoUnit.DAYS.between(LocalDate.now(), presentGoal.getTargetDate());
        Boolean isOver = LocalDate.now().isAfter(presentGoal.getTargetDate());
        UserResponseGoalDto goalDto = UserResponseGoalDto.of(presentGoal.getDuringGoalAmount(), presentGoal.getDuringGoalCount(), presentGoal.getTargetMoney(), targetDay, dDay, isOver, presentGoal.isAttained());
        return UserResponseDto.of(userDto, goalDto);
    }

    @Transactional
    public void updateNickname(Long userId, UpdateUserNicknameDto requestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        user.updateNickname(requestDto.getNickname());
    }
    @Transactional
    public void updateFcmToken(Long userId, UpdateFcmTokenDto updateFcmTokenDto){
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        user.updateFcmToken(user.getFcmToken());
    }

    public Boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}

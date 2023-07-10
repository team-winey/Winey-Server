package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.user.UserResponseDto;
import org.winey.server.controller.response.user.UserResponseGoalDto;
import org.winey.server.controller.response.user.UserResponseUserDto;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Transactional
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

        int targetDay = (int) Duration.between(presentGoal.getTargetDate(), presentGoal.getCreatedAt().toLocalDate()).toDays();
        UserResponseGoalDto goalDto = UserResponseGoalDto.of(presentGoal.getDuringGoalAmount(), presentGoal.getDuringGoalCount(), presentGoal.getTargetMoney(), targetDay, LocalDate.now().isAfter(presentGoal.getTargetDate()), presentGoal.isAttained());

        return UserResponseDto.of(userDto, goalDto);
    }
}

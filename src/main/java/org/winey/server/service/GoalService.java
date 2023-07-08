package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.goal.GoalRequestCreateDto;
import org.winey.server.controller.response.goal.GoalResponseCreateDto;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoalResponseCreateDto createGoal(GoalRequestCreateDto requestDto, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        LocalDate targetDate = LocalDate.now().plusDays(requestDto.getTargetDay());
        Goal createGoal = goalRepository.save(Goal.builder()
                        .targetMoney(requestDto.getTargetMoney())
                        .targetDate(targetDate)
                        .user(user)
                .build());

        return GoalResponseCreateDto.of(userId, createGoal.getTargetMoney(), createGoal.getTargetDate(), createGoal.getCreatedAt());
    }
}

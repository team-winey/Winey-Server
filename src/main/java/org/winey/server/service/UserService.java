package org.winey.server.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.UpdateFcmTokenDto;
import org.winey.server.controller.request.UpdateUserNicknameDto;
import org.winey.server.controller.response.user.GetAchievementStatusResponseDto;
import org.winey.server.controller.response.user.UserResponseDto;
import org.winey.server.domain.user.User;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    @Transactional
    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
                Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        LocalDateTime hundredDaysAgo = LocalDateTime.now().minusDays(100);
        Long amountSavedHundredDays = feedRepository.getSavedAmountForPeriod(user, hundredDaysAgo);
        Long amountSavedTwoWeeks = feedRepository.getSavedAmountForPeriod(user, twoWeeksAgo);
        Long amountSpentTwoWeeks = feedRepository.getSpentAmountForPeriod(user, twoWeeksAgo);

        return UserResponseDto.of(user.getUserId(), user.getNickname(),
            user.getUserLevel().getName(), user.getFcmIsAllowed(), user.getSavedAmount(),
            amountSavedHundredDays == null ? 0L : amountSavedHundredDays,
            amountSavedTwoWeeks == null ? 0L : amountSavedTwoWeeks,
            amountSpentTwoWeeks == null ? 0L : amountSpentTwoWeeks
        );
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

    @Transactional(readOnly = true)
    public GetAchievementStatusResponseDto getAchievementStatus(Long userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION,
                Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        UserLevel nextUserLevel = UserLevel.getNextUserLevel(user.getUserLevel());

        if (nextUserLevel == null) {
            return GetAchievementStatusResponseDto.of(user.getUserLevel(), 0L, 0L);
        }

        long remainingAmount = nextUserLevel.getMinimumAmount() - user.getSavedAmount();
        long remainingCount = nextUserLevel.getMinimumCount() - user.getSavedCount();
        return GetAchievementStatusResponseDto.of(
            user.getUserLevel(),
            remainingAmount < 0 ? 0L : remainingAmount,
            remainingCount < 0 ? 0L : remainingCount
        );
    }

    public Boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}

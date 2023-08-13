package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.feed.GetAllFeedResponseDto;
import org.winey.server.controller.response.feed.GetFeedResponseDto;
import org.winey.server.controller.response.notification.GetAllNotiResponseDto;
import org.winey.server.controller.response.notification.GetNotiResponseDto;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedLikeRepository;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotiService {
    private final NotiRepository notiRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public GetAllNotiResponseDto getAllNoti(Long userId) {
        User currentUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<Notification> notifications = notiRepository.findAllByNotiReceiverOrderByCreatedAtDesc(currentUser);
        if (notifications.isEmpty()){
            return null;
        }
        List<GetNotiResponseDto> response = notifications.stream()
                .map(noti -> GetNotiResponseDto.of(
                        noti.getNotiId(),
                        noti.getNotiReceiver().getNickname(),
                        noti.getNotiMessage(),
                        noti.getNotiType(),
                        noti.isChecked(),
                        noti.getLinkId(),
                        noti.getCreatedAt()
                )).collect(Collectors.toList());
        return GetAllNotiResponseDto.of(response);
    }
    @Transactional(readOnly = true)
    public void checkAllNoti(Long userId) {     // 내가 체크 안했던 애들을 찾아서 다 체크 true 해버리기. 특정 조건 url을 타면 ㅇㅇ
        User currentUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<Notification> notifications = notiRepository.findByNotiReceiverAndIsCheckedFalse(currentUser);
        notifications.stream().forEach
                (notification -> {
                    notification.updateIsChecked();
                });
    }

    @Transactional(readOnly = true)
    public Boolean checkNewNoti(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        List<Notification> notifications = notiRepository.findByNotiReceiverAndIsCheckedFalse(user);
        return notifications.size() != 0;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkGoalDateNotification() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        List<Goal> allGoals = goalRepository.findLatestGoalsForEachUser();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (Goal currentGoal : allGoals) {
            if (currentGoal.getTargetDate().isEqual(today.minusDays(1))) {
                Notification notification = Notification.builder()
                        .notiType(NotiType.GOALFAILED)
                        .notiReciver(currentGoal.getUser())
                        .notiMessage(NotiType.GOALFAILED.getType())
                        .isChecked(false)
                        .build();
                notification.updateLinkId(null);
                notiRepository.save(notification);
            }
        }
    }
}

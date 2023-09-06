package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.notification.GetAllNotiResponseDto;
import org.winey.server.controller.response.notification.GetNotiResponseDto;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
        if (notifications.isEmpty()) {
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
                        getTimeAgo(noti.getCreatedAt()),
                        noti.getCreatedAt()
                )).collect(Collectors.toList());
        return GetAllNotiResponseDto.of(response);
    }

    @Transactional
    public void checkAllNoti(Long userId) {     // 내가 체크 안했던 애들을 찾아서 다 체크 true 해버리기. 특정 조건 url을 타면 ㅇㅇ
        User currentUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<Notification> notifications = notiRepository.findByNotiReceiverAndIsCheckedFalse(currentUser);
        notifications.forEach(Notification::updateIsChecked);
    }

    @Transactional(readOnly = true)
    public Boolean checkNewNoti(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        List<Notification> notifications = notiRepository.findByNotiReceiverAndIsCheckedFalse(user);
        return notifications.size() != 0;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    @Transactional
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

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul") //혹시 모를 racing에 대비해 새벽 2시에 시작되도록 함.
    @Transactional
    public void deleteReadNotification() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        List<Notification> notifications = notiRepository.findByIsCheckedTrueOrderByUpdatedAtAsc();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        List<Long> deleteLists = new ArrayList<>();
        System.out.println(now.minusDays(7)+"일 전을 기준으로 체크된 모든 알림을 지웁니다.");

        for (Notification notification : notifications) {
            if (notification.getUpdatedAt().isBefore(now.minusDays(7))) {
                if(notification.getNotiType() != NotiType.HOWTOLEVELUP) {   //단, 레벨업 방법 알림은 제외.
                    deleteLists.add(notification.getNotiId());
                }
            } else {
                break;
            }
        }
        if (!deleteLists.isEmpty()) {
            Long res = notiRepository.deleteAllByNotiIdIn(deleteLists);
            System.out.println(res+"개의 알림을 삭제했습니다.");
        }
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.YEARS.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.YEARS.between(now, createdAt)) + "년전";
        }
        if (ChronoUnit.MONTHS.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.MONTHS.between(now, createdAt)) + "달전";
        }
        if (ChronoUnit.WEEKS.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.WEEKS.between(now, createdAt)) + "주전";
        }
        if (ChronoUnit.DAYS.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.DAYS.between(now, createdAt)) + "일전";
        }
        if (ChronoUnit.HOURS.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.HOURS.between(now, createdAt)) + "시간전";
        }
        if (ChronoUnit.MINUTES.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.MINUTES.between(now, createdAt)) + "분전";
        }
        if (ChronoUnit.SECONDS.between(now, createdAt) != 0) {
            return Math.abs(ChronoUnit.SECONDS.between(now, createdAt)) + "초전";
        }
        return "지금";
    }
}

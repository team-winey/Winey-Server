package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.feed.GetAllFeedResponseDto;
import org.winey.server.controller.response.feed.GetFeedResponseDto;
import org.winey.server.controller.response.notification.GetAllNotiResponseDto;
import org.winey.server.controller.response.notification.GetNotiResponseDto;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedLikeRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotiService {
    private final NotiRepository notiRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetAllNotiResponseDto getAllNoti(Long userId) {
        User currentUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<Notification> notifications = notiRepository.findAllByUserOrderByCreatedAtDesc(currentUser);
        List<GetNotiResponseDto> response = notifications.stream()
                .map(noti -> GetNotiResponseDto.of(
                        noti.getNotiId(),
                        noti.getNotiSender().getNickname(),
                        noti.getNotiReciver().getNickname(),
                        noti.getNotiMessage(),
                        noti.getNotiType(),
                        noti.isChecked(),
                        noti.getLinkId() == null ? 0 : noti.getLinkId()
                )).collect(Collectors.toList());
        return GetAllNotiResponseDto.of(response);
    }

    public void checkAllNoti(Long userId) {     // 내가 체크 안했던 애들을 찾아서 다 체크 true 해버리기. 특정 조건 url을 타면 ㅇㅇ
        User currentUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<Notification> notifications = notiRepository.findByNotiReciverAndIsCheckedFalse(currentUser);
        notifications.stream().forEach
                (notification -> {
                    notification.updateIsChecked();
                    notiRepository.save(notification);
                });
    }
}

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
                        noti.getNotiUser().getNickname(),
                        noti.getNotiMessage(),
                        noti.getNotiType(),
                        noti.isChecked(),
                        noti.getLinkId()
                )).collect(Collectors.toList());
        return GetAllNotiResponseDto.of(response);
    }
}

package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.common.message.MessageQueueSender;
import org.winey.server.service.message.FcmRequestDto;
import org.winey.server.controller.response.feedLike.CreateFeedLikeResponseDto;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedLike;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedLikeRepository;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedLikeService {
    private final FeedLikeRepository feedLikeRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    private final NotiRepository notiRepository;

    private final MessageQueueSender messageQueueSender;

    @Transactional
    public CreateFeedLikeResponseDto createFeedLike(Long userId, Long feedId, boolean feedLike) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION, Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));
        Boolean isLiked = feedLikeRepository.existsByFeedAndUser(feed, user);
        // 에러 언제 ? -> feedLike = true && isLiked = true
        // feedLike = false && isLiked = false
        if (isLiked == feedLike) {
            throw new BadRequestException(Error.REQUEST_VALIDATION_EXCEPTION, Error.REQUEST_VALIDATION_EXCEPTION.getMessage());
        }

        if (feedLike) { // 좋아요 생성
            FeedLike like = FeedLike.builder()
                    .feed(feed)
                    .user(user)
                    .build();
            feedLikeRepository.save(like);

            if (user.getUserId() != feed.getUser().getUserId()){    //만약 좋아요를 누르는 사람이랑 피드 주인이랑 다르면 알림 생성
                createNotificationInLike(feed, like, user);
            }
        } else { // 좋아요 취소
            FeedLike deletedFeedLike = feedLikeRepository.deleteByFeedAndUser(feed, user).get(0);

            // 관련 알림 삭제
            if (user.getUserId() != feed.getUser().getUserId()) {   //만약 좋아요를 누르는 사람이랑 피드 주인이랑 다르면 알림 삭제
                notiRepository.deleteByNotiTypeAndResponseId(NotiType.LIKENOTI, deletedFeedLike.getId());
            }
        }
        return CreateFeedLikeResponseDto.of(feedId, feedLike, (long) feedLikeRepository.countByFeed(feed));
    }

    private void createNotificationInLike(Feed feed, FeedLike like, User user) {
        Notification notification = Notification.builder()  // 좋아요 알림 생성
            .notiType(NotiType.LIKENOTI)
            .notiMessage(user.getNickname()+NotiType.LIKENOTI.getType())
            .isChecked(false)
            .notiReciver(feed.getUser())
            .build();
        notification.updateLinkId(feed.getFeedId());
        notification.updateResponseId(like.getId());
        notification.updateRequestUserId(user.getUserId());
        notiRepository.save(notification);
        if (feed.getUser().getFcmIsAllowed() && !notification.getNotiReceiver().getFcmToken().isEmpty()) { //푸시알림에 동의했을 경우. 피드 주인에게 알림
            messageQueueSender.pushSender(
                FcmRequestDto.of(
                    notification.getNotiMessage(),
                    notification.getNotiReceiver().getFcmToken(),
                    notification.getNotiType(),
                    feed.getFeedId()));
        }
    }
}

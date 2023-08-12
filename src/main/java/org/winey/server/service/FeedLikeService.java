package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

            Notification noti = Notification.builder()  // 좋아요 알림 생성
                    .notiType(NotiType.LIKENOTI)
                    .notiMessage(user.getNickname()+NotiType.LIKENOTI.getType())
                    .isChecked(false)
                    .notiReciver(feed.getUser())
                    .build();
            noti.updateLinkId(feedId);
            notiRepository.save(noti);
        } else { // 좋아요 취소
            feedLikeRepository.deleteByFeedAndUser(feed, user);
            //여기에 알림 삭제를 박아야할 것 같은데 DDD agregate root를 적용하면 어쩌구.. 할수있다는데 잘 모르겠음.
        }

        return CreateFeedLikeResponseDto.of(feedId, feedLike, (long) feedLikeRepository.countByFeed(feed));
    }
}

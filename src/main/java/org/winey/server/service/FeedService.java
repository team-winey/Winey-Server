package org.winey.server.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.CreateFeedRequestDto;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.comment.CommentResponseDto;
import org.winey.server.controller.response.feed.CreateFeedResponseDto;
import org.winey.server.controller.response.feed.GetAllFeedResponseDto;
import org.winey.server.controller.response.feed.GetFeedDetailResponseDto;
import org.winey.server.controller.response.feed.GetFeedResponseDto;
import org.winey.server.domain.block.BlockUser;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedType;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.exception.model.UnauthorizedException;
import org.winey.server.infrastructure.BlockUserRepository;
import org.winey.server.infrastructure.CommentRepository;
import org.winey.server.infrastructure.FeedLikeRepository;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final CommentRepository commentRepository;
    private final NotiRepository notiRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public CreateFeedResponseDto createFeed(CreateFeedRequestDto request, Long userId, String imageUrl) {
        // 1. 유저를 가져온다.
        User presentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // 2. 피드 타입 값을 받아와 올바른 피드유형인가 판별
        String feedType = request.getFeedType();
//        if (!FeedType.isValidFeedType(feedType))
//            throw new BadRequestException(Error.INVALID_FEEDTYPE, Error.INVALID_FEEDTYPE.getMessage());

        // 3. 피드를 생성한다.
        Feed feed = Feed.builder()
            .feedImage(imageUrl)
            .feedType(feedType == null || feedType.isEmpty() ? null : FeedType.valueOf(feedType))
            .feedMoney(request.getFeedMoney())
            .feedTitle(request.getFeedTitle())
            .user(presentUser)
            .build();

        feedRepository.save(feed);

        // 4. 유저의 피드 유형이 절약인 경우 누적 절약 금액과 누적 절약 피드 개수 업데이트한다.
        if (Objects.equals(feedType, "SAVE")) presentUser.increaseSavedAmountAndCount(feed.getFeedMoney());
        
        // 5. 레벨업을 체크한다.
        UserLevel newUserLevel = UserLevel.calculateUserLevel(presentUser.getSavedAmount(), presentUser.getSavedCount());

        // 레벨업 달성 여부 담는 Bool 값
        Boolean levelUpgraded = false;

        if (presentUser.getUserLevel() != newUserLevel) {
            // 4-1. 레벨업한다.
            presentUser.updateUserLevel(newUserLevel);

            // 4-2. 레벨업 달성 여부를 true로 수정
            levelUpgraded = true;

            // 4-3. 레벨업 알림을 생성한다.
            switch (newUserLevel) {
                case KNIGHT:
                    notificationBuilderInFeed(NotiType.RANKUPTO2, presentUser);
                    break;
                case ARISTOCRAT:
                    notificationBuilderInFeed(NotiType.RANKUPTO3, presentUser);
                    break;
                case EMPEROR:
                    notificationBuilderInFeed(NotiType.RANKUPTO4, presentUser);
                    break;
                default:
                    break;
            }
        }

        return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt(), levelUpgraded);
    }

    @Transactional
    public String deleteFeed(Long userId, Long feedId) {
        // 1. 유저를 가져온다.
        User presentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // 2. 지우고자 하는 피드를 가져온다.
        Feed wantDeleteFeed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION, Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));

        // 3. 피드를 작성한 유저와 현재 접속한 유저가 다르면 삭제할 수 없다.
        if (presentUser != wantDeleteFeed.getUser()) {
            throw new UnauthorizedException(Error.DELETE_UNAUTHORIZED, Error.DELETE_UNAUTHORIZED.getMessage()); // 삭제하는 사람 아니면 삭제 못함 처리.
        }

        // 4. 절약 피드가 삭제되면 유저의 누적 절약 금액과 누적 절약 피드 개수 감소
        if (wantDeleteFeed.getFeedType() == FeedType.SAVE) {
            presentUser.decreaseSavedAmountAndCount(wantDeleteFeed.getFeedMoney());
        }

        // 5. 레벨다운을 체크한다.
        UserLevel newUserLevel = UserLevel.calculateUserLevel(presentUser.getSavedAmount(), presentUser.getSavedCount());

        if (presentUser.getUserLevel() != newUserLevel) {
            // 4-1. 레벨다운한다.
            presentUser.updateUserLevel(newUserLevel);

            // 4-2. 레벨다운 알림을 생성한다.
            switch (newUserLevel) {
                case COMMONER:
                    notificationBuilderInFeed(NotiType.DELETERANKDOWNTO1, presentUser);
                    break;
                case KNIGHT:
                    notificationBuilderInFeed(NotiType.DELETERANKDOWNTO2, presentUser);
                    break;
                case ARISTOCRAT:
                    notificationBuilderInFeed(NotiType.DELETERANKDOWNTO3, presentUser);
                    break;
                default:
                    break;
            }
        }

        // 6. 피드를 삭제한다.
        notiRepository.deleteByLinkId(feedId);
        feedRepository.delete(wantDeleteFeed);
        return wantDeleteFeed.getFeedImage();
    }

    @Transactional(readOnly = true)
    public GetAllFeedResponseDto getAllFeed(int page, Long userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        PageRequest pageRequest = PageRequest.of(page - 1, 20);

        List<User> blockUsers = blockUserRepository.findByRequestUser(user).stream()
            .map(BlockUser::getResponseUser)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        Page<Feed> feedPage;

        if (blockUsers.size() == 0) {
            feedPage = feedRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        } else {
            feedPage = feedRepository.findByUserNotInOrderByCreatedAtDesc(blockUsers, pageRequest);
        }

        PageResponseDto pageInfo = PageResponseDto.of(feedPage.getTotalPages(), feedPage.getNumber() + 1, (feedPage.getTotalPages() == feedPage.getNumber() + 1));
        List<GetFeedResponseDto> feeds = feedPage.stream()
                .map(feed -> GetFeedResponseDto.of(
                        feed.getFeedId(),
                        feed.getUser().getUserId(),
                        feed.getUser().getNickname(),
                        feed.getUser().getUserLevel().getLevelNumber(),
                        feed.getFeedType() == null ? null : feed.getFeedType().getStringVal(),
                        feed.getFeedTitle(),
                        feed.getFeedImage(),
                        feed.getFeedMoney(),
                        feedLikeRepository.existsByFeedAndUser(feed, user), //현재 접속한 유저가 좋아요 눌렀는지
                        (long) feedLikeRepository.countByFeed(feed), //해당 피드의 좋아요 개수 세기.
                        commentRepository.countByFeed(feed),
                        getTimeAgo(feed.getCreatedAt()),
                        feed.getCreatedAt()                 //해당 피드 만든 날짜 localdate로 바꿔서 주기.
                )).collect(Collectors.toList());
        return GetAllFeedResponseDto.of(pageInfo, feeds);
    }

    @Transactional(readOnly = true)
    public GetAllFeedResponseDto getMyFeed(int page, Long userId) {
        User myUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<Feed> myFeedPage = feedRepository.findAllByUserOrderByCreatedAtDesc(myUser, pageRequest);
        PageResponseDto pageInfo = PageResponseDto.of(myFeedPage.getTotalPages(), myFeedPage.getNumber() + 1, (myFeedPage.getTotalPages() == myFeedPage.getNumber() + 1));
        List<GetFeedResponseDto> feeds = myFeedPage.stream()
                .map(myFeed -> GetFeedResponseDto.of(
                        myFeed.getFeedId(),
                        myFeed.getUser().getUserId(),
                        myFeed.getUser().getNickname(),
                        myFeed.getUser().getUserLevel().getLevelNumber(),
                        myFeed.getFeedType() == null ? null : myFeed.getFeedType().getStringVal(),
                        myFeed.getFeedTitle(),
                        myFeed.getFeedImage(),
                        myFeed.getFeedMoney(),
                        feedLikeRepository.existsByFeedAndUser(myFeed, myUser), //현재 접속한 유저가 좋아요 눌렀는지
                        (long) feedLikeRepository.countByFeed(myFeed),              //해당 피드의 좋아요 개수 세기.
                        commentRepository.countByFeed(myFeed),
                        getTimeAgo(myFeed.getCreatedAt()),
                        myFeed.getCreatedAt()                 //해당 피드 만든 날짜 localdate로 바꿔서 주기.
                )).collect(Collectors.toList());
        return GetAllFeedResponseDto.of(pageInfo, feeds);
    }

    @Transactional(readOnly = true)
    public GetFeedDetailResponseDto getFeedDetail(Long feedId, Long userId) {
        Feed detailFeed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION, Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));
        User connectedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<CommentResponseDto> comments = commentRepository.findAllByFeedOrderByCreatedAtAsc(detailFeed)
                .stream().map(comment -> CommentResponseDto.of(
                        comment.getCommentId(),
                        comment.getUser().getUserId(),
                        comment.getUser().getNickname(),
                        comment.getContent(),
                        comment.getUser().getUserLevel().getLevelNumber(),
                        comment.getCreatedAt()
                )).collect(Collectors.toList());

        GetFeedResponseDto detailResponse = GetFeedResponseDto.of(
                detailFeed.getFeedId(),
                detailFeed.getUser().getUserId(),
                detailFeed.getUser().getNickname(),
                detailFeed.getUser().getUserLevel().getLevelNumber(),
                detailFeed.getFeedType() == null ? null : detailFeed.getFeedType().getStringVal(),
                detailFeed.getFeedTitle(),
                detailFeed.getFeedImage(),
                detailFeed.getFeedMoney(),
                feedLikeRepository.existsByFeedAndUser(detailFeed, connectedUser), //현재 접속한 유저가 detail feed에 좋아요 눌렀는지
                (long) feedLikeRepository.countByFeed(detailFeed),              //해당 피드의 좋아요 개수 세기.
                commentRepository.countByFeed(detailFeed),
                getTimeAgo(detailFeed.getCreatedAt()),
                detailFeed.getCreatedAt()                  //해당 피드 만든 날짜 localdate로 바꿔서 주기.
        );

        return GetFeedDetailResponseDto.of(detailResponse, comments);

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

    private void notificationBuilderInFeed(NotiType type, User user){
        Notification notification = Notification.builder()
            .notiType(type)
            .notiMessage(type.getType())
            .isChecked(false)
            .notiReciver(user)
            .build();
        notification.updateLinkId(null);
        notiRepository.save(notification);
    }
}

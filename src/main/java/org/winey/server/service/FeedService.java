package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.CreateFeedRequestDto;
import org.winey.server.controller.response.PageResponseDto;
import org.winey.server.controller.response.comment.GetCommentResponseDto;
import org.winey.server.controller.response.feed.*;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.ForbiddenException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.exception.model.UnauthorizedException;
import org.winey.server.infrastructure.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private final GoalRepository goalRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final CommentRepository commentRepository;

    private final NotiRepository notiRepository;

    @Transactional
    public CreateFeedResponseDto createFeed(CreateFeedRequestDto request, Long userId, String imageUrl) {
        User presentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        Feed feed = Feed.builder()
                .feedImage(imageUrl)
                .feedMoney(request.getFeedMoney())
                .feedTitle(request.getFeedTitle())
                .user(presentUser)
                .build();
        feedRepository.save(feed);
        Goal myGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser).stream().findFirst()
                .orElseThrow(() -> new ForbiddenException(Error.FEED_FORBIDDEN_EXCEPTION, Error.FEED_FORBIDDEN_EXCEPTION.getMessage())); //목표 설정 안하면 피드 못만듬 -> 에러처리
        myGoal.updateGoalCountAndAmount(feed.getFeedMoney(), true); // 절약 금액, 피드 횟수 업데이트.

        if (myGoal.isAttained()) {
            System.out.println("이미 목표달성");
            return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt());
        }
        if (LocalDate.now().isAfter(myGoal.getTargetDate())){
            System.out.println("목표를 제한 시간 내에 이루지 못함.");
            throw new ForbiddenException(Error.FEED_FORBIDDEN_EXCEPTION, Error.FEED_FORBIDDEN_EXCEPTION.getMessage()); //목표 설정 새로 하게 유도.
        }
        if (myGoal.getDuringGoalAmount() >= myGoal.getTargetMoney()) {
            myGoal.updateIsAttained(true); // 달성여부 체크
            if (presentUser.getUserLevel().getLevelNumber() != checkUserLevelUp(presentUser)) {//userLevel 변동사항 체크, 만약에 레벨에 변동이 생겼다면? 레벨 강등 알림 생성.
                switch (checkUserLevelUp(presentUser)){
                    case 2:
                        Notification noti2 = Notification.builder()
                                .notiType(NotiType.RANKUPTO2)
                                .notiMessage(NotiType.RANKUPTO2.getType())
                                .isChecked(false)
                                .notiReciver(presentUser)
                                .build();
                        noti2.updateLinkId(null);
                        notiRepository.save(noti2);
                        break;
                    case 3:
                        Notification noti3 = Notification.builder()
                                .notiType(NotiType.RANKUPTO3)
                                .notiMessage(NotiType.RANKUPTO3.getType())
                                .isChecked(false)
                                .notiReciver(presentUser)
                                .build();
                        noti3.updateLinkId(null);
                        notiRepository.save(noti3);
                        break;
                    case 4:
                        Notification noti4 = Notification.builder()
                                .notiType(NotiType.RANKUPTO4)
                                .notiMessage(NotiType.RANKUPTO4.getType())
                                .isChecked(false)
                                .notiReciver(presentUser)
                                .build();
                        noti4.updateLinkId(null);
                        notiRepository.save(noti4);
                        break;

                }
            }
        }
        return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt());
    }

    private int checkUserLevelUp(User presentUser) {
        int userAchievedGoals = goalRepository.countByUserAndIsAttained(presentUser, true); //Goal 중 userid가 맞고 isAttained true 개수 세기
        if (userAchievedGoals < 1) {
            presentUser.updateUserLevel(UserLevel.COMMONER);
            return 1;
        } else if (userAchievedGoals < 3) {
            presentUser.updateUserLevel(UserLevel.KNIGHT);
            return 2;
        } else if (userAchievedGoals < 9) {
            presentUser.updateUserLevel(UserLevel.ARISTOCRAT);
            return 3;
        } else {
            presentUser.updateUserLevel(UserLevel.EMPEROR);
            return 4;
        }
    }

    @Transactional
    public String deleteFeed(Long userId, Long feedId) {
        User presentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        Goal presentGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_GOAL_EXCEPTION, Error.NOT_FOUND_GOAL_EXCEPTION.getMessage()));
        Feed wantDeleteFeed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_FEED_EXCEPTION, Error.NOT_FOUND_FEED_EXCEPTION.getMessage()));

        if (presentUser != wantDeleteFeed.getUser()) {
            throw new UnauthorizedException(Error.DELETE_UNAUTHORIZED, Error.DELETE_UNAUTHORIZED.getMessage()); // 삭제하는 사람 아니면 삭제 못함 처리.
        }

        if ((!presentGoal.getCreatedAt().isBefore(wantDeleteFeed.getCreatedAt())) || (!presentGoal.getTargetDate().isAfter(wantDeleteFeed.getCreatedAt().toLocalDate()))) {
            feedRepository.delete(wantDeleteFeed);
            return wantDeleteFeed.getFeedImage();
        }
        presentGoal.updateGoalCountAndAmount(wantDeleteFeed.getFeedMoney(), false);
        if (presentUser.getUserLevel().getLevelNumber() >= 3 && (presentGoal.getTargetMoney() > presentGoal.getDuringGoalAmount())) { //귀족 이상이면 강등로직.
            presentGoal.updateIsAttained(false); // 달성여부 체크
            if (presentUser.getUserLevel().getLevelNumber() != checkUserLevelUp(presentUser)) {//userLevel 변동사항 체크, 만약에 레벨에 변동이 생겼다면? 레벨 강등 알림 생성.
                switch (checkUserLevelUp(presentUser)){
                    case 3:
                        Notification noti3 = Notification.builder()
                                .notiType(NotiType.DELETERANKDOWNTO3)
                                .notiMessage(NotiType.DELETERANKDOWNTO3.getType())
                                .isChecked(false)
                                .notiReciver(presentUser)
                                .build();
                        noti3.updateLinkId(null);
                        notiRepository.save(noti3);
                        break;
                    case 2:
                        Notification noti2 = Notification.builder()
                                .notiType(NotiType.DELETERANKDOWNTO2)
                                .notiMessage(NotiType.DELETERANKDOWNTO2.getType())
                                .isChecked(false)
                                .notiReciver(presentUser)
                                .build();
                        noti2.updateLinkId(null);
                        notiRepository.save(noti2);
                        break;
                }
            }
        }
        feedRepository.delete(wantDeleteFeed);
        return wantDeleteFeed.getFeedImage();
    }

    @Transactional(readOnly = true)
    public GetAllFeedResponseDto getAllFeed(int page, Long userId) {
        PageRequest pageRequest = PageRequest.of(page - 1, 20);
        Page<Feed> feedPage = feedRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        PageResponseDto pageInfo = PageResponseDto.of(feedPage.getTotalPages(), feedPage.getNumber() + 1, (feedPage.getTotalPages() == feedPage.getNumber() + 1));
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
        List<GetFeedResponseDto> feeds = feedPage.stream()
                .map(feed -> GetFeedResponseDto.of(
                        feed.getFeedId(),
                        feed.getUser().getUserId(),
                        feed.getUser().getNickname(),
                        feed.getUser().getUserLevel().getLevelNumber(),
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
        List<GetCommentResponseDto> comments = commentRepository.findAllByFeedOrderByCreatedAtDesc(detailFeed)
                .stream().map(comment -> GetCommentResponseDto.of(
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
}

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
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.domain.user.User;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.ForbiddenException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.exception.model.UnauthorizedException;
import org.winey.server.infrastructure.BlockUserRepository;
import org.winey.server.infrastructure.CommentRepository;
import org.winey.server.infrastructure.FeedLikeRepository;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.NotiRepository;
import org.winey.server.infrastructure.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final CommentRepository commentRepository;
    private final NotiRepository notiRepository;
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public CreateFeedResponseDto createFeed(CreateFeedRequestDto request, Long userId, String imageUrl) {
        // 1. 유저를 가져온다.
        User presentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // 2. 해당 유저의 가장 최신 목표를 가져온다.
        Goal myGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser).stream().findFirst()
            .orElseThrow(() -> new ForbiddenException(Error.FEED_FORBIDDEN_EXCEPTION, Error.FEED_FORBIDDEN_EXCEPTION.getMessage())); //목표 설정 안하면 피드 못만듬 -> 에러처리

        // 3. 피드를 생성한다.
        Feed feed = Feed.builder()
                .feedImage(imageUrl)
                .feedMoney(request.getFeedMoney())
                .feedTitle(request.getFeedTitle())
                .user(presentUser)
                .goal(myGoal)
                .build();
        feedRepository.save(feed);

        // 4. 목표의 duringGoalAmount, duringGoalCount 를 업데이트한다.
        myGoal.updateGoalCountAndAmount(feed.getFeedMoney(), true); // 절약 금액, 피드 횟수 업데이트

        // 5. 이미 모든 레벨을 마스터한 사용자는 넘어간다.
        if (myGoal.isAttained() && presentUser.getUserLevel() == UserLevel.EMPEROR) {
            System.out.println("이미 목표달성");
            return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt());
        }

        // 6. 목표 달성 여부를 체크한다.
        if (myGoal.getDuringGoalAmount() >= myGoal.getTargetMoney()) {
            // 6-1. 해당 목표의 달성 여부를 true 로 바꾼다.
            myGoal.updateIsAttained(true);

            // 6-2. 레벨을 올린다.
            presentUser.upgradeUserLevel();

            // 6-3. 레벨업 알림을 생성한다.
            switch (presentUser.getUserLevel()) {
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
        return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt());
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

        // 현재 삭제하고자 하는 피드의 goal 아이디 != 현재 진행 중인 goal 아이디 --> 넘어가!
        if (wantDeleteFeed.getGoal().getGoalId() != presentGoal.getGoalId()) {
            feedRepository.delete(wantDeleteFeed);
            return wantDeleteFeed.getFeedImage();
        }

//        if ((!presentGoal.getCreatedAt().isBefore(wantDeleteFeed.getCreatedAt())) || (!presentGoal.getTargetDate().isAfter(wantDeleteFeed.getCreatedAt().toLocalDate()))) {
//            feedRepository.delete(wantDeleteFeed);
//            return wantDeleteFeed.getFeedImage();
//        }

        presentGoal.updateGoalCountAndAmount(wantDeleteFeed.getFeedMoney(), false);

        if (presentUser.getUserLevel().getLevelNumber() >= 3 && (presentGoal.getTargetMoney() > presentGoal.getDuringGoalAmount())) { //귀족 이상이면 강등로직.
            presentGoal.updateIsAttained(false); // 달성여부 체크
            if (presentUser.getUserLevel().getLevelNumber() != checkUserLevelUp(presentUser)) {//userLevel 변동사항 체크, 만약에 레벨에 변동이 생겼다면? 레벨 강등 알림 생성.
                switch (checkUserLevelUp(presentUser)){
                    case 3:
                        notificationBuilderInFeed(NotiType.DELETERANKDOWNTO3, presentUser);
                        break;
                    case 2:
                        notificationBuilderInFeed(NotiType.DELETERANKDOWNTO2, presentUser);
                        break;
                }
            }
        }
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
}

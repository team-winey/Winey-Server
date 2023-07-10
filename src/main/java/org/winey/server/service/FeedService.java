package org.winey.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.controller.request.CreateFeedRequestDto;
import org.winey.server.controller.response.feed.CreateFeedResponseDto;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;
import org.winey.server.domain.user.UserLevel;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.ForbiddenException;
import org.winey.server.exception.model.NotFoundException;
import org.winey.server.infrastructure.FeedRepository;
import org.winey.server.infrastructure.GoalRepository;
import org.winey.server.infrastructure.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private final GoalRepository goalRepository;

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

        if (myGoal.isAttained() || LocalDate.now().isAfter(myGoal.getTargetDate())) {
            System.out.println("이미 목표달성 or 목표일자 넘김 or 목표금액 넘김");
            return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt());
        }
        if (myGoal.getDuringGoalAmount() >= myGoal.getTargetMoney()) {
            myGoal.updateIsAttained(true); // 달성여부 체크
            checkUserLevelUp(presentUser); // userLevel 변동사항 체크
        }
        return CreateFeedResponseDto.of(feed.getFeedId(), feed.getCreatedAt());
    }

    private void checkUserLevelUp(User presentUser) {
        int userAchievedGoals = goalRepository.countByUserAndIsAttained(presentUser, true); //Goal 중 userid가 맞고 isAttained true 개수 세기
        if (userAchievedGoals < 1) {
            presentUser.updateUserLevel(UserLevel.COMMONER);
        } else if (userAchievedGoals < 3) {
            presentUser.updateUserLevel(UserLevel.KNIGHT);
        } else if (userAchievedGoals < 9) {
            presentUser.updateUserLevel(UserLevel.ARISTOCRAT);
        } else {
            presentUser.updateUserLevel(UserLevel.EMPEROR);
        }
    }

    @Transactional
    public String deleteFeed(Long userId, Long feedId) {
        User presentUser = userRepository.findByUserId(userId).get();
        Goal presentGoal = goalRepository.findByUserOrderByCreatedAtDesc(presentUser).stream().findFirst().get();
        Feed wantDeleteFeed = feedRepository.findByFeedId(feedId).get();
        if (presentGoal.getCreatedAt() == null) {
            new BadRequestException(Error.NOT_FOUND_CREATED_AT_EXCEPTION, Error.NOT_FOUND_CREATED_AT_EXCEPTION.getMessage());
        } else if ((!presentGoal.getCreatedAt().isBefore(wantDeleteFeed.getCreatedAt())) || (!presentGoal.getTargetDate().isAfter(wantDeleteFeed.getCreatedAt().toLocalDate()))) {
            feedRepository.delete(wantDeleteFeed);
            return wantDeleteFeed.getFeedImage();
        }
        presentGoal.updateGoalCountAndAmount(wantDeleteFeed.getFeedMoney(), false);
        if (presentGoal.getTargetMoney() > presentGoal.getDuringGoalAmount()) { //현재까지 누적 + 피드가격으로 업데이트된 금액 >= 목표금액
            presentGoal.updateIsAttained(false); // 달성여부 체크
            checkUserLevelUp(presentUser); // userLevel 변동사항 체크
        }
        feedRepository.delete(wantDeleteFeed);
        return wantDeleteFeed.getFeedImage();


    }
}
